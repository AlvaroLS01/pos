package com.comerzzia.iskaypet.pos.util.date;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	private static final Logger log = Logger.getLogger(DateUtils.class);

	public static final String PATRON_FECHA_LARGA = "dd-MM-yyyy HH:mm:ss";

	public static Date parseDate(String dateString, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.parse(dateString);
		}
		catch (ParseException parseException) {
			log.error("El formato de fecha no es válido.", parseException);
			throw new RuntimeException(parseException);
		}
	}

	public static String formatDate(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static Date addTimeUnits(Date date, int amount, int timeUnit) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(timeUnit, amount);
		return cal.getTime();
	}

	public static Date addYears(Date date, int years) {
		return addTimeUnits(date, years, Calendar.YEAR);
	}

	public static Date addDays(Date date, int days) {
		return addTimeUnits(date, days, Calendar.DAY_OF_YEAR);
	}

	public static Date addMonths(Date date, int months) {
		return addTimeUnits(date, months, Calendar.MONTH);
	}

	public static Date getCurrentDateWithZeroTime() {
		LocalDateTime localDateTime = LocalDateTime.now()
				.withHour(0)
				.withMinute(0)
				.withSecond(0)
				.withNano(0);



		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Date getCurrentDateWithLastTime() {
		LocalDateTime localDateTime = LocalDateTime.now()
				.withHour(23)
				.withMinute(59)
				.withSecond(59)
				.withNano(59);

		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

}
