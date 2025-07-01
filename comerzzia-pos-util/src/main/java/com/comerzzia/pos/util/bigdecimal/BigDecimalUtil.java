package com.comerzzia.pos.util.bigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtil {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    
    /** 
     * Calculates the specified percentage of a quantity.
     * Example: getPercentage(80,20) = 16 // 20% of 80 = 16
     * @param quantity
     * @param percentage
     * @return 
     */
	public static BigDecimal getPercentageValue(BigDecimal quantity, BigDecimal percentage) {
		return quantity.multiply(percentage).divide(ONE_HUNDRED);
	}

    /** 
     * Returns the result of subtracting the specified percentage to a quantity.
     * Example: subtractPercentage(80,20) = 64 // 80 - 20% = 64
     * @param quantity
     * @param percentage
     * @return 
     */
	public static BigDecimal subtractPercentage(BigDecimal quantity, BigDecimal percentage) {
		return quantity.subtract(getPercentageValue(quantity, percentage));
	}
    
    /** 
     * Returns the result of adding the specified percentage to a quantity.
     * Example: addPercentage(80,20) = 96 // 80 + 20% = 96
     * @param quantity
     * @param percentage
     * @return 
     */
	public static BigDecimal addPercentage(BigDecimal quantity, BigDecimal percentage) {
		return quantity.add(getPercentageValue(quantity, percentage));
	}
    
	public static BigDecimal subtractPercentageRounded(BigDecimal quantity, BigDecimal percentage) {
		return round(quantity.subtract(getPercentageValueRounded(quantity, percentage)));
	}

	public static BigDecimal addPercentageRounded(BigDecimal quantity, BigDecimal percentage) {
		return round(quantity.add(getPercentageValue(quantity, percentage)));
	}

	public static BigDecimal subtractPercentageRounded4(BigDecimal quantity, BigDecimal percentage) {
		return round4(quantity.subtract(getPercentageValueRounded4(quantity, percentage)));
	}

	public static BigDecimal addPercentageRounded4(BigDecimal quantity, BigDecimal percentage) {
		return round4(quantity.add(getPercentageValueRounded4(quantity, percentage)));
	}

	public static BigDecimal getPercentageValueRounded(BigDecimal quantity, BigDecimal percentage) {
		return round(getPercentageValue(quantity, percentage));
	}

	public static BigDecimal getPercentageValueRounded4(BigDecimal quantity, BigDecimal percentage) {
		return round4(getPercentageValue(quantity, percentage));
	}

	public static BigDecimal round(BigDecimal quantity) {
		if (quantity == null) {
			return null;
		}

		return round(quantity, 2, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal round4(BigDecimal quantity) {
		if (quantity == null) {
			return null;
		}
		return round(quantity, 4, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal round(BigDecimal quantity, int decimals) {
		if (quantity == null) {
			return null;
		}
		return round(quantity, decimals, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal round(BigDecimal quantity, int decimals, int roundingMode) {
		if (quantity == null) {
			return null;
		}
		return quantity.setScale(decimals, roundingMode);
	}
    
    /** 
     * Returns the quantity of which, after applying the specified percentage, results in the specified quantity.
     * ejemplo: getBeforePercentage(112, 12) = 100 // 100 + 12% = 112
     * @param quantity
     * @param percentage
     */
	public static BigDecimal getBeforePercentage(BigDecimal quantity, BigDecimal percentage) {
		return quantity.multiply(ONE_HUNDRED).divide(ONE_HUNDRED.add(percentage), 4, RoundingMode.HALF_UP);
	}

	public static BigDecimal getBeforePercentage(BigDecimal quantity, BigDecimal percentage, int scale) {
		return quantity.multiply(ONE_HUNDRED).divide(ONE_HUNDRED.add(percentage), scale, RoundingMode.HALF_UP);
	}

	public static BigDecimal getBeforePercentageRounded(BigDecimal quantity, BigDecimal percentage) {
		return round(getBeforePercentage(quantity, percentage));
	}        

    /** 
     * Returns the percentage which represents a quantity of a greater quantity. 
     * Example: getPercentage(50, 20) = 40 // 40% of 50 = 20
     * @param greaterQuantity
     * @param quantity
     */
    public static BigDecimal getPercentage(BigDecimal greaterQuantity, BigDecimal quantity) {
        return (quantity.multiply(ONE_HUNDRED)).divide(greaterQuantity, 4, RoundingMode.HALF_UP);
    }

    /** 
     * Returns the percentage subtracted from a quantity to obtain another quantity.
     * Example: getTantoPorCientoMenos(50, 20) = 60 // 50 - 60% = 20
     * @param sourceQuantity
     * @param resultQuantity
     */
	public static BigDecimal getSubtractedPercentage(BigDecimal sourceQuantity, BigDecimal resultQuantity) {
		if (BigDecimalUtil.isEqualsToZero(sourceQuantity)) {
			return BigDecimal.ZERO;
		}
		return (sourceQuantity.subtract(resultQuantity)).multiply(ONE_HUNDRED).divide(sourceQuantity, 2, RoundingMode.HALF_UP);
	}

	public static BigDecimal multiply(BigDecimal bigDecimal, Integer integer) {
		return bigDecimal.multiply(new BigDecimal(integer));
	}

	public static boolean isLess(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) < 0;
	}

	public static boolean isLessThanZero(BigDecimal a) {
		return a.compareTo(BigDecimal.ZERO) < 0;
	}

	public static boolean isGreater(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) > 0;
	}

	public static boolean isGreaterThanZero(BigDecimal a) {
		return a.compareTo(BigDecimal.ZERO) > 0;
	}

	public static boolean isEquals(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) == 0;
	}

	public static boolean isEqualsToZero(BigDecimal a) {
		return a.compareTo(BigDecimal.ZERO) == 0;
	}

	public static boolean isLessOrEquals(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) <= 0;
	}

	public static boolean isLessOrEqualsToZero(BigDecimal a) {
		return a.compareTo(BigDecimal.ZERO) <= 0;
	}

	public static boolean isGreaterOrEquals(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) >= 0;
	}

	public static boolean isGreaterOrEqualsToZero(BigDecimal a) {
		return a.compareTo(BigDecimal.ZERO) >= 0;
	}
	
}
