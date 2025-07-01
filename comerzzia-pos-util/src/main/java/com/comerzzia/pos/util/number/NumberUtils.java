package com.comerzzia.pos.util.number;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberUtils {

    private NumberUtils() {
    }
    
    /** 
     * Null safe equals for Double. Returns true if both are null.
     * @param d1
     * @param d2
     * @return boolean
     */
    public static boolean equals(Double d1, Double d2){
    	if (d1==null && d2==null){
    		return true;
    	}
    	if ((d1==null && d2!=null) || (d2==null && d1!=null)){
    		return false;
    	}
    	return d1.equals(d2);
    }

    public static Double round(double d, int decimals) {
        if (d==0){
        	return 0.0;
        }
    	return new BigDecimal(d).setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    public static Integer getFractionalPart(Double d, int decimals){
    	return getWholeNumber((d - getWholeNumber(d)) * Math.pow(10, decimals));
    }

    public static Integer getWholeNumber(Double d){
    	return d.intValue();
    }

    /** 
     * Returns Double from String. If any error happens during parsing, default value will be returned.
     * @param number
     * @param defaultValue
     * @return Double
     */
    public static Double unformatDouble(String number, Double defaultValue) {
        if (number == null){
        	return defaultValue;
        }
        DecimalFormat formatter = createFormatter();
        try { 
        	defaultValue = formatter.parse(number).doubleValue();
        } 
        catch (ParseException e1) { 
        } 

        return defaultValue;
    }

    
    /** 
     * Returns Long from String. If any error happens during parsing, default value will be returned.
     * @param number
     * @param defaultValue
     * @return Long
     */
    public static Long unformatLong(String number, Long defaultValue) {
        if (number == null){
        	return defaultValue;
        }
        DecimalFormat formatter = createFormatter();
        try { 
        	defaultValue = formatter.parse(number).longValue();
        } 
        catch (ParseException e1) { 
        } 

        return defaultValue;
    }
    
    /** 
     * Returns Integer from String. If any error happens during parsing, default value will be returned.
     * @param number
     * @param defaultValue
     * @return Integer
     */
    public static Integer unformatInteger(String number, Integer defaultValue){
        if (number == null){
        	return defaultValue;
        }
        DecimalFormat formatter = createFormatter();
        try { 
        	defaultValue = formatter.parse(number).intValue();
        } 
        catch (ParseException e1) { 
        } 

        return defaultValue;    	
    }

    /** 
     * Returns String with specified format from number.
     * 
     * @param number
     * @param decimals
     * @return String
     */
    public static String format(Object number, int decimals){
		
		DecimalFormat formatter = createFormatter();
		
		// Set fraction digits
		formatter.setMinimumFractionDigits(decimals);
		formatter.setMaximumFractionDigits(decimals);
		
		return formatter.format(number);
    }
    
    public static String format(Object value){
		
		DecimalFormat formatter = createFormatter();
		formatter.setMaximumFractionDigits(4);
		
		return formatter.format(value);
    }

	private static DecimalFormat createFormatter() {
		// Create formatter for our location
    	DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance();
		
		return formatter;
	}
	
	/** 
	 * Returns Integer from String. If any error happens during parsing, default value will be returned.
	 * @param number
	 * @return Integer 
	 */
	public static Integer getInteger(String number, Integer defaultValue){
		Integer i = null;
		try{
			i = getInteger(number);
		}
		catch(ParseException e){
			i = defaultValue;
		}
		return i;
	}
	
	public static Integer getInteger(String number) throws ParseException{
		Integer i = null;
		try{
			i = Integer.parseInt(number);
		}
		catch(Exception e){
			throw new ParseException("The specified value is not an integer.", 0);			
		}
		return i;
	}
	
}