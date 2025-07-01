package com.comerzzia.pos.util.format;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.efaps.number2words.IConverter;
import org.efaps.number2words.converters.English;
import org.efaps.number2words.converters.German;
import org.efaps.number2words.converters.Spanish;

import com.comerzzia.pos.util.bigdecimal.BigDecimalUtil;
import com.comerzzia.pos.util.config.AppConfig;

public class FormatUtils {

	// Log
	private static final Logger log = Logger.getLogger(FormatUtils.class.getName());

	// Instance
	private static FormatUtils instance;

	// Formatters
	private static DateFormat dateFormatter;
	private static DateFormat timeFormatter;

	private static DecimalFormat numberFormatter;
	private static DecimalFormat amountFormatter;
	private static DecimalFormat currencyFormatter;

	private static DateFormat documentDateFormatter;
	
	private static IConverter textFormatter;

	private static final String DATE_TIME_SEPARATOR = " - ";
	private static final String DATE_HOUR_DOT_SEPARATOR = " · ";
	
	public static Currency getCurrency() {
		return currencyFormatter.getCurrency();
	}

	/**
	 * Returns the only instance of the class.
	 *
	 * @return
	 */
	public static FormatUtils getInstance() {
		if (instance == null) {
			instance = new FormatUtils();
		}
		return instance;
	}

	/**
	 * Initialize formatters with configuration values.
	 * 
	 * @param locale - Used Locale
	 */
	public void init(Locale locale) {
		// Date and Time formatters
		dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		if (AppConfig.getCurrentConfiguration().getDateFormat() != null) {
			try {
				dateFormatter = new SimpleDateFormat(AppConfig.getCurrentConfiguration().getDateFormat(), locale);
			}
			catch (IllegalArgumentException e) {
				log.error("The date format is not valid.");
			}
		}
		timeFormatter = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
		if (AppConfig.getCurrentConfiguration().getTimeFormat() != null) {
			try {
				timeFormatter = new SimpleDateFormat(AppConfig.getCurrentConfiguration().getTimeFormat(), locale);
			}
			catch (IllegalArgumentException e) {
				log.error("The time format is not valid.");
			}
		}

		// Special formatters
		documentDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale);

		DecimalFormatSymbols symbols = generateDecimalFormatSymbols(locale, false);
		
		// Number formatters
		NumberFormat numberNumberFormat = NumberFormat.getInstance(locale);
		numberFormatter = (DecimalFormat) numberNumberFormat;
		numberFormatter.setParseBigDecimal(true);
		numberFormatter.setDecimalFormatSymbols(symbols);

		// Using an instance of Currency only to know the decimal digits 
		// because if this instance is used to format it would not allow to modify the decimal separator.
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
		
		// Amount formatters
		NumberFormat amountNumberFormat = NumberFormat.getInstance(locale);
		amountFormatter = configureAmountNumberFormat(amountNumberFormat, currencyFormat);
		amountFormatter.setDecimalFormatSymbols(symbols);
		
		// Currency amount formatters
		NumberFormat currencyNumberFormat = NumberFormat.getCurrencyInstance(locale);
		currencyFormatter = configureAmountNumberFormat(currencyNumberFormat, currencyFormat);
		DecimalFormatSymbols currencySymbols = generateDecimalFormatSymbols(locale, true);
		currencyFormatter.setDecimalFormatSymbols(currencySymbols);
		
		textFormatter = initializeTextFormatter(locale);

	}
	
	protected IConverter initializeTextFormatter(Locale locale){
        IConverter converter = null;
        if(locale.getLanguage() != null){
        	if(locale.getLanguage().startsWith("de")){
        		 converter = new German();
        	}
        	else if(locale.getLanguage().startsWith("en")){
        		converter = new English();
        	}
        	else if(locale.getLanguage().startsWith("es")){
        		converter = new Spanish();
        	}
        }
        return converter;
    } 

	protected DecimalFormat configureAmountNumberFormat(NumberFormat amountNumberFormat, NumberFormat currencyFormat) {
		DecimalFormat ammountFormatter = (DecimalFormat) amountNumberFormat;
		ammountFormatter.setMaximumFractionDigits(currencyFormat.getMaximumFractionDigits());
		ammountFormatter.setMinimumFractionDigits(currencyFormat.getMinimumFractionDigits());
		ammountFormatter.setRoundingMode(RoundingMode.HALF_UP);
		ammountFormatter.setParseBigDecimal(true);

		if (AppConfig.getCurrentConfiguration().getDecimalNumbers() != null) {
			ammountFormatter.setMaximumFractionDigits(AppConfig.getCurrentConfiguration().getDecimalNumbers());
			ammountFormatter.setMinimumFractionDigits(AppConfig.getCurrentConfiguration().getDecimalNumbers());
		}
		return ammountFormatter;
	}

	protected DecimalFormatSymbols generateDecimalFormatSymbols(Locale locale, boolean useCurrency) {
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
		if (!useCurrency) {
			symbols.setCurrencySymbol("");
		}
		if (AppConfig.getCurrentConfiguration().getDecimalSeparator() != null || AppConfig.getCurrentConfiguration().getGroupSeparator() != null) {

			if (AppConfig.getCurrentConfiguration().getDecimalSeparator() != null) {
				symbols.setDecimalSeparator(AppConfig.getCurrentConfiguration().getDecimalSeparator().charAt(0));
			}

			if (AppConfig.getCurrentConfiguration().getGroupSeparator() != null) {
				symbols.setGroupingSeparator(AppConfig.getCurrentConfiguration().getGroupSeparator().charAt(0));
			}
		}
		return symbols;
	}
	
	/**
	 * Formats number to string assuming the number is an amount.
	 * 
	 * @param amount
	 * @return
	 */
	public String formatAmount(BigDecimal amount) {
		return amountFormatter.format(amount);
	}

	/**
	 * Formats number to string assuming the number is an amount with currency.
	 * 
	 * @param amount
	 * @return
	 */
	public String formatCurrencyAmount(BigDecimal amount) {
		return currencyFormatter.format(amount);
	}

	/**
	 * Parses string to BigDecimal assuming string is an amount.
	 * 
	 * @param amount
	 * @return
	 */
	public BigDecimal parseAmount(String amount) {
		BigDecimal res = null;
		ParsePosition pos = new ParsePosition(0);
		res = (BigDecimal) numberFormatter.parse(amount, pos);
		if(pos.getIndex() != amount.length() || pos.getErrorIndex() != -1){
			return null;
		}else{
			return res;
		}
	}

	/**
	 * Formats number to string without decimals.
	 *
	 * @param number
	 * @return
	 */
	public String formatNumber(BigDecimal number) {
		return formatNumber(number, 0);
	}

	/**
	 * Formats number to string with the specified number of decimals.
	 *
	 * @param number
	 * @param decimals
	 * @return
	 */
	public String formatNumber(BigDecimal number, Integer decimals) {
		if (decimals != null && decimals >= 0) {
			numberFormatter.setMinimumFractionDigits(decimals);
			numberFormatter.setMaximumFractionDigits(decimals);
		}
		else { // If number has decimals, it should be configured. In any case, we allow between 0 and 4 decimals.
			numberFormatter.setMinimumFractionDigits(0);
			numberFormatter.setMaximumFractionDigits(4);
		}
		return numberFormatter.format(number);
	}
	
	public String formatRoundedNumber(BigDecimal numero, Integer decimales) {
		return formatNumber(numero.setScale(decimales, BigDecimal.ROUND_HALF_UP), decimales);
	}

	public String formatNumberWithoutSeparator(BigDecimal numero) {
		return numero.toString().replace(".", "");
	}

	/**
	 * Formats date to string.
	 * 
	 * @param date
	 * @return
	 */
	public String formatDate(Date date) {
		return dateFormatter.format(date);
	}

	public String formatDocumentDateTime(Date date) {
		return documentDateFormatter.format(date);
	}

	/**
	 * Formats time to string.
	 * 
	 * @param date
	 * @return
	 */
	public String formatTime(Date date) {
		return timeFormatter.format(date);
	}

	/**
	 * Parses string to time.
	 * 
	 * @param date
	 * @return
	 */
	public Date parseTime(String date) {
		try {
			return timeFormatter.parse(date);
		}
		catch (ParseException ex) {
			log.error("parseTime()- Error parsing date. Field must be previously validated. Date: " + date);
		}
		return null;
	}

	/**
	 * Formats date time to string.
	 * 
	 * @param date
	 * @return
	 */
	public String formatDateTime(Date date) {
		return dateFormatter.format(date) + DATE_TIME_SEPARATOR + timeFormatter.format(date);
	}

	public Date parseDateTime(String date) {
		return parseDateTime(date, false);
	}
	
	/**
	 * Parses string to date time.
	 * 
	 * @param strDate
	 * @return
	 */
	public Date parseDateTime(String strDate, boolean currentTime) {
		try {
			Date date;
			Date time;
			if (!currentTime) {
				String[] aux = strDate.split(DATE_TIME_SEPARATOR);
				date = dateFormatter.parse(aux[0]);
				time = timeFormatter.parse(aux[1]);
			}
			else {
				date = parseDate(strDate);
				if (date == null) {
					return null;
				}
				Calendar timeCalendar = Calendar.getInstance();
				timeCalendar.setTime(new Date());
				time = timeCalendar.getTime();
			}

			Calendar dateCalendar = Calendar.getInstance();
			dateCalendar.setTime(date);
			Calendar timeCalendar = Calendar.getInstance();
			timeCalendar.setTime(time);
			dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
			dateCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));

			return dateCalendar.getTime();
		}
		catch (ParseException ex) {
			log.error("parseDateTime()- Error desformateando fecha corta. El campo se ha de validar previamente. F: " + strDate);
		}
		return null;
	}

	/**
	 * Parses string to date using short style pattern.
	 * 
	 * @param date string which represents a date in short style pattern
	 * @return date
	 */
	public Date parseDate(String date) {
		ParsePosition pos = new ParsePosition(0);
		Date parse = dateFormatter.parse(date, pos);
		if (pos.getIndex() != date.length() || pos.getErrorIndex() != -1) {
			return null;
		}
		else {
			return parse;
		}
	}

	public Date parseDocumentDateTime(String date) {
		ParsePosition pos = new ParsePosition(0);
		Date parse = documentDateFormatter.parse(date, pos);
		if (pos.getIndex() != date.length() || pos.getErrorIndex() != -1) {
			return null;
		}
		else {
			return parse;
		}
	}

	/**
	 * Parses String to Long
	 * 
	 * @param text string which represents a Long
	 */
	public Long parseLong(String text) {
		Long res = null;
		if (!text.isEmpty()) {
			res = Long.parseLong(text);
		}
		return res;
	}

	/**
	 * Parses String to BigDecimal
	 * 
	 * @param text string which represents a BigDecimal
	 */
	public BigDecimal parseBigDecimal(String text) {
		BigDecimal res = null;
		ParsePosition pos = new ParsePosition(0);
		res = (BigDecimal) numberFormatter.parse(text, pos);
		if (pos.getIndex() != text.length() || pos.getErrorIndex() != -1) {
			return null;
		}
		else {
			return res;
		}
	}

	/**
	 * Parses String to BigDecimal with the specified number of decimals.
	 * 
	 * @param text string which represents a BigDecimal
	 */
	public BigDecimal parseBigDecimal(String text, int decimals) {
		BigDecimal res = null;
		res = parseBigDecimal(text);
		if (res != null) {
			res = BigDecimalUtil.round(res, decimals);
		}
		return res;
	}

	public char getDecimalSeparator() {
		return numberFormatter.getDecimalFormatSymbols().getDecimalSeparator();
	}
	
	/**
	 * Returns a copy of the text adding the specified number of spaces to the left.
	 * 
	 * @param text
	 * @param spaces
	 */
	public String addSpacesLeft(String text, int spaces) {
		String res = text;
		for (int i = 0; i < spaces; i++) {
			res = " " + res;
		}
		return res;
	}

	/**
	 * Returns a copy of the text adding the specified number of spaces to the right.
	 * 
	 * @param text
	 * @param spaces
	 */
	public String addSpacesRight(String text, int spaces) {
		String res = text;
		for (int i = 0; i < spaces; i++) {
			res = res + " ";
		}
		return res;
	}

	/**
	 * Formats number to string and add zeros to the left until string size is reached.
	 * 
	 * @param value Not null
	 * @param charactersNumber
	 */
	public String completeZerosLeft(Long value, int charactersNumber) {
		String result = "" + value;
		return completeZerosLeft(result, charactersNumber);
	}

	/**
	 * Returns a copy of the text adding zeros to the left until string size is reached.
	 * 
	 * @param text Not null
	 * @param charactersNumber
	 */
	public String completeZerosLeft(String text, int charactersNumber) {
		String resultado = text;
		if (resultado.length() < charactersNumber) {
			while (resultado.length() < charactersNumber) {
				resultado = "0" + resultado;
			}
		}
		return resultado;
	}
	
	/**
	 * Returns a copy of the text adjusting the number of zeros on its left to the specified number of characters.
	 * 
	 * @param text Not null
	 * @param charactersNumber
	 */
	public String adjustZerosLeft(String text, int charactersNumber) {
		String result = text;
		if (result.length() < charactersNumber) {
			while (result.length() < charactersNumber) {
				result = "0" + result;
			}
		}
		else if (result.length() > charactersNumber) {
			result = text.substring(result.length() - charactersNumber);
		}
		return result;
	}
	
	public int getFractionDigits() {
		return amountFormatter.getMaximumFractionDigits();
	}
	
	public String getDatePattern() {
		return ((SimpleDateFormat) dateFormatter).toPattern();
	}

	public String formatNumberToText(Long number){
		String res = "";
		if(textFormatter != null && number != null){
			res = textFormatter.convert(number);
		}
		return res;
	}
	
	public String formatNumberToText(BigDecimal number){	
		String result = "";
		if (textFormatter != null && number != null) {
			String separator = selectSeparator();
			String numberString = number.toPlainString();
			String[] arrayNums = numberString.split("\\.");
			Long wholeNumber = Long.parseLong(arrayNums[0]);
			Long fractionalPart = null;
			if (arrayNums.length > 1) {
				fractionalPart = Long.parseLong(arrayNums[1]);
			}
			result = textFormatter.convert(wholeNumber);
			if (fractionalPart != null && fractionalPart != 0.0) {
				result += separator + textFormatter.convert(fractionalPart);
			}
		}
		return result;
	}
	
	protected String selectSeparator() {
		String separator = "";
		if (textFormatter instanceof Spanish) {
			separator = " con ";
		}
		else if (textFormatter instanceof English) {
			separator = " and ";
		}
		else if (textFormatter instanceof German) {
			separator = " ";
		}
		else {
			separator = " ";
		}
		return separator;
	}
	
	/**
	 * Returns the string representation of a date without displaying seconds 
	 * 
	 * @param date
	 * @return String
	 */
	public String dateHourNoSecondsFormatter(Date date) {
		return dateFormatter.format(date) + DATE_HOUR_DOT_SEPARATOR + timeFormatter.format(date).substring(0, timeFormatter.format(date).lastIndexOf(":"));
	}
	
	/**
	 * Returns the representation of a concept without the last part with the document code
	 * 
	 * @param String
	 * @return String
	 */
	public String conceptNoDocumentFormatter(String concept) {
		String noDocument = concept.substring(0, concept.length() - 9);

		String[] aux = noDocument.split(":");

		return aux[0].substring(0, 1) + aux[0].substring(1).toLowerCase() + ":" + aux[1];
	}
	
	/**
	 * Returns the representation of a document without the first two slashes content, showing only the last number
	 * 
	 * @param String
	 * @return String
	 */
	public String onlyDocumentNumberFormatter(String document) {
		return document.split("/")[2];
	}
	
	/**
	 * Capitalize the first letter of a String
	 * 
	 * @param String
	 * @return String
	 */
	public String capitalizeFirstLetter(String s) {
		String res = "";

		res += s.substring(0, 1).toUpperCase();
		res += s.substring(1).toLowerCase();

		return res;
	}
	
	/**
	 * Sets default msg when document not found for moves which does not have one
	 * 
	 * @param String
	 * @return String
	 */
	public String documentOrBlank(String doc) {
		if (doc == null) {
			return " ";
		}
		else {
			return doc;
		}
	}
	
}
