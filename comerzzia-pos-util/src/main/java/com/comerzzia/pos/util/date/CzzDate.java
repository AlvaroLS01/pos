package com.comerzzia.pos.util.date;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public class CzzDate implements Comparable<CzzDate> { 

	/** Pattern for dates of type dd/MM/yyyy */
	public final static String FORMAT_DATE = "dd/MM/yyyy";
	
	/** Pattern for dates of type dd/MM/yyyy - HH:mm */
	public final static String FORMAT_DATE_TIME = "dd/MM/yyyy - HH:mm";
	
	/** Pattern for dates of type dd/MM/yyyy - HH:mm:ss */
	public final static String FORMAT_DATE_TIME_WITH_SECONDS = "dd/MM/yyyy - HH:mm:ss";

	/** Pattern for dates of type yyyyMMdd_HHmmss */
	public final static String FORMAT_TIMESTAMP = "yyyyMMdd_HHmmss";
	
	private Date date;
	
	/** Default constructor. Initialize date with current day. */
	public CzzDate() {
		this(new Date());
	}
	
	/** 
	 * Constructor from Date.
	 */
	public CzzDate(Date date) {
		this.date = date;
	}
	
	/** 
	 * Constructor from String (using {@value #FORMAT_DATE} format).
	 * If any error happens during parsing, date remains null.
	 * @param strDate String with a date in {@value #FORMAT_DATE} format
	 */
	public CzzDate(String strDate){
		this(strDate, FORMAT_DATE);
	}

	/** 
	 * Constructor from String (using specified format).
	 * If any error happens during parsing, date remains null.
	 * @param strDate String with a date in specific format
	 */
	public CzzDate(String strDate, String format){
		this(getDate(strDate, format));
	}
	
	private static Date getDate(String strDate, String format){
        if (StringUtils.isNotEmpty(strDate)){
	        try {
	            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
	            df.setLenient(false);
	            return df.parse(strDate);
	        }
	        catch (ParseException e) {
	        }
        }
        return null;
	}
	
	/** Wrapped Date. */
	public Date getDate() {
		return date;
	}

	/** Set wrapped Date.*/
	public void setDate(Date date) {
		this.date = date;
	}
	
	// Instance operations ---------------------------------------------------------------------
	
	/** 
	 * Returns new instance of CzzDate from Date. If date is null, returns null.
	 */
	public static CzzDate getCzzDate(Date date){
		return getCzzDate(date, null);
	}

	/** 
	 * Returns new instance of CzzDate from Date. If date is null, returns the default value.
	 */
	public static CzzDate getCzzDate(Date date, CzzDate defaultValue){
		if (date != null){
			return new CzzDate(date);
		}
		return defaultValue;
	}

	/** 
	 *  Returns new instance of CzzDate from String using specified format. 
	 *  If string is null, empty or has an invalid format, returns null.
	 */
	public static CzzDate getCzzDate(String strDate, String format){
		if (StringUtils.isNotEmpty(strDate)){
			CzzDate date = new CzzDate(strDate, format);
			if (date.getDate()!=null){
				return date;
			}
		}
		return null;
	}
	
	/** 
	 *  Returns new instance of CzzDate from String (using {@value #FORMAT_DATE} format). 
	 *  If string is null, empty or has an invalid format, returns null.
	 */
	public static CzzDate getCzzDate(String strDate){
		if (StringUtils.isNotEmpty(strDate)){
			CzzDate date = getCzzDate(strDate, FORMAT_DATE);
			if (date.getDate()!=null){
				return date;
			}
		}
		return null;
	}
	
	/** 
	 * Returns new instance of CzzDate from String (using {@value #FORMAT_DATE} format). 
	 * If string is null, empty or has an invalid format, returns the default value.
	 */
	public static CzzDate getCzzDate(String strDate, CzzDate defaultValue){
		CzzDate date = getCzzDate(strDate);
		if (date == null){
			return defaultValue;
		}
		return date;
	}	

	/** 
	 * Returns new instance of CzzDate from date and time as strings (using {@value #FORMAT_DATE_TIME} format).
	 * If string is null, empty or has an invalid format, returns null.
	 */
	public static CzzDate getCzzDateTime(String strDate, String strTime){
		if (StringUtils.isNotEmpty(strDate)){
			CzzDate date = new CzzDate(strDate + " - " + strTime, FORMAT_DATE_TIME);
			if (date.getDate()!=null){
				return date;
			}
		}
		return null;
	}
	
	/** 
	 * Returns new instance of CzzDate from date and time as strings (using {@value #FORMAT_DATE_TIME} format).
	 * If string is null, empty or has an invalid format, returns the default value.
	 */
	public static CzzDate getCzzDateTime(String strDate, String strTime, CzzDate defaultValue){
		CzzDate date = getCzzDateTime(strDate, strTime);
		if (date == null){
			return defaultValue;
		}
		return date;
	}		
	
	// Validation operations---------------------------------------------------------------------
	
	/** 
	 * Validate string which represents a date using {@value #FORMAT_DATE} format.
	 * <p>Internally calls {@link #validateDate(String, String) validateDate}.</p>
	 */
	public static boolean validateDate(String fecha){
        return validateDate(fecha, FORMAT_DATE);
	}

	/** 
	 * Validate string which represents a date using the specified format.
	 * <p>The {@link java.text.SimpleDateFormat SimpleDateFormat} formatter is told to stick to a strict date 
	 * and not add days if the number of days is greater than the given month.</p>
	 */
	public static boolean validateDate(String strDate, String format){
        if (StringUtils.isNotEmpty(strDate)){
	        try {
	            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
	            df.setLenient(false);
	            df.parse(strDate);
	        }
	        catch (ParseException e) {
	        	return false;
	        }
        }
        return true;
	}
	
	// Serialization operations-----------------------------------------------------------------
	
	/** Returns wrapped date as String using {@value #FORMAT_DATE} format. */
	public String getString(){
		return getString(FORMAT_DATE);
	}

	/** Returns wrapped date as String using {@value #FORMAT_DATE_TIME} format. */
	public String getStringDateTime(){
		return getString(FORMAT_DATE_TIME);
	}

	/** Returns wrapped date as String using {@value #FORMAT_DATE_TIME_WITH_SECONDS} format. */
	public String getStringDateTimeWithSeconds(){
		return getString(FORMAT_DATE_TIME_WITH_SECONDS);
	}

	/** Returns wrapped date as String using {@value #FORMAT_TIMESTAMP} format. */ 
	public String getStringTimestamp(){
		return getString(FORMAT_TIMESTAMP);
	}
	
	/** Returns wrapped date as String using specified format. */
	public String getString(String format){
        Format formatter = new SimpleDateFormat(format);
        if (date != null) {
            return formatter.format(date);
        }
       return null;
	}
	
	@Override
	public String toString(){
		return getString();
	}
	
	// Date components operations-------------------------------------------------------------
	
	public Integer getDay(){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);
	}
	
	public Integer getMonth(){  
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH)+1;
	}

	public Integer getYear(){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);
	}
	
	public String getDayOfWeek(){
		SimpleDateFormat formateador = new SimpleDateFormat("EEEE"); 
		return formateador.format(date);
	}
	public String getDayOfWeek(Locale locale){
		SimpleDateFormat formateador = new SimpleDateFormat("EEEE",locale); 
		return formateador.format(date);
	}
	
	public String getMonthName(){
		SimpleDateFormat formateador = new SimpleDateFormat("MMMMM"); 
		return formateador.format(date);
		
	}
	public String getMonthName(Locale locale){
		SimpleDateFormat formateador = new SimpleDateFormat("MMMMM", locale);
		return formateador.format(date);
	}
	
	public void addDays(int days){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, days);
		date = c.getTime();
	}
	
	public void addMonths(int months){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, months);
		date = c.getTime();
	}
	
	public void addYears(int years){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, years);
		date = c.getTime();
	}
	
	public void addHouts(int hours){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR, hours);
		date = c.getTime();
	}
	
	public void addMinutes(int minutes){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE, minutes);
		date = c.getTime();
	}
	
	// Comparison operations---------------------------------------------------------------------
	
	public int differenceOfDays(CzzDate date){
		Long ms = this.date.getTime() - date.getDate().getTime();
		return Math.abs(new Long(ms / 1000 / 60 / 60 / 24).intValue());
	}
	
	public boolean after(CzzDate date){
		return this.date.after(date.getDate());
	}
	
	public boolean before(CzzDate date){
		return this.date.before(date.getDate());
	}
	
	public boolean afterOrEquals(CzzDate date){
		return this.date.after(date.getDate()) || equals(date);
	}
	
	public boolean beforeOrEquals(CzzDate date){
		return this.date.before(date.getDate()) || equals(date);
	}

	/** 
	 * Compare wrapped Date with another Date.
	 * <p>If the Object is an instance of CzzDate, the result will be the same as doing this.date.equals(o.getDate()).</p>
	 * @param o - CzzDate or Date
	 */
	@Override
	public boolean equals(Object o){
		if (o == null){
			return false;
		}
		if (o instanceof CzzDate){
			CzzDate f = (CzzDate)o;
			return this.date.equals(f.getDate());
		}
		if (o instanceof Date){
			return this.date.equals(o);
		}
		return false;
	}

	/** Compare wrapped Date with another Date ignoring time. */
	public boolean equalsDate(CzzDate date){
		return date.getYear().equals(getYear()) && date.getMonth().equals(getMonth()) && date.getDay().equals(getDay());
	}

	@Override
	public int compareTo(CzzDate date) {
		return getDate().compareTo(date.getDate());
	}
	
	// Other operations-------------------------------------------------------------------------
	
	public java.sql.Date getSQL() {
		return new java.sql.Date(date.getTime());
	}
	
	public Timestamp getTimestamp(){
		return new Timestamp(getDate().getTime());
	}

}
