package customer.tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberTool {

  public static String format(Object number, String format) {
    if (number instanceof String) {
      number = toDouble(number);
    }
    if (number == null)
      return null;
    DecimalFormat decimalFormat = new DecimalFormat(format);
    return decimalFormat.format(number);
  }

  public static Double toDouble(Object data) {
    return toDouble(data, null);
  }

  public static Double toDouble(Object data, Double nullTo) {
    if (data != null) {
      if (data instanceof Double) {
        return (Double) data;
      } else if (data.toString().equals("")) {
        return null;
      } else {
        return Double.valueOf(data.toString());
      }
    }
    return nullTo;
  }

  /**
   * 获取货币的精度，如果是日元5，其余2
   * 
   * @param currency
   * @return
   */
  public static BigDecimal toScale(BigDecimal data, String currency) {
    BigDecimal prc = BigDecimal.ZERO;
    if (data != null) {
      if (UWebConstants.JPY.equals(currency)) {
        prc = data.setScale(5);
      } else {
        prc = data.setScale(2);
      }
    }
    return prc;
  }

  // // 在库计算小数
  // public static BigDecimal getDigital(BigDecimal a) {
  // if (a != null)
  // return a.setScale(
  // MmssConstants.INV_DECIMAL_DIGIT,
  // BigDecimal.ROUND_HALF_UP);

  // return a;
  // }

  /**
   * integer [1, MAX)
   */
  public static final String REGEX_POSITIVE_INTEGER = "^[1-9]\\d*$"; //$NON-NLS-1$

  /**
   * decimal (0.0, MAX)
   */
  public static final String REGEX_POSITIVE_DECIMAL = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$"; //$NON-NLS-1$

  /**
   * decimal + integer [0, MAX)
   */
  public static final String REGEX_NON_NEGATIVE_REAL_NUMBER = "^\\+?(\\d+|\\d+\\.\\d+)$"; //$NON-NLS-1$

  // 去除后面的=0
  public static String getNoZero(String qty) {
    if (qty == null)
      return qty;
    if (qty.indexOf(".") > 0) {
      // 正则表达
      qty = qty.replaceAll("0+?$", ""); // 去掉后面无用的零
      qty = qty.replaceAll("[.]$", ""); // 如小数点后面全是零则去掉小数点
    }
    return qty;
  }

  // 加上千分位
  public static String Thousand(String str) {
    BigDecimal data = new BigDecimal(str);
    NumberFormat numberFormat1 = NumberFormat.getNumberInstance();
    return numberFormat1.format(data); // 结果是11,122.33
  }

  // 去0 加上千分位
  public static String getRealQty(String qty) {
    if (StringTool.isNull(qty))
      return "";
    return Thousand(getNoZero(qty));
  }

  private static boolean isMatch(String regex, String orginal) {
    if (orginal == null || orginal.trim().equals("")) { //$NON-NLS-1$
      return false;
    }
    Pattern pattern = Pattern.compile(regex);
    Matcher isNum = pattern.matcher(orginal);
    return isNum.matches();
  }

  /**
   * 正整数[1,MAX)
   *
   * @param orginal
   * @Description: 是否为正整数
   * @return boolean
   */
  public static boolean isPositiveInteger(String orginal) {
    return isMatch(REGEX_POSITIVE_INTEGER, orginal);
  }

  /**
   * 非负实数 [0, MAX)
   *
   * @param orginal
   * @return boolean
   */
  public static boolean isNonNegativeRealNumber(String orginal) {
    return isMatch(REGEX_NON_NEGATIVE_REAL_NUMBER, orginal);
  }

  /**
   * 正小数 (0.0, MAX)
   *
   * @param orginal
   * @return boolean
   */
  public static boolean isPositiveDecimal(String orginal) {
    return isMatch(REGEX_POSITIVE_DECIMAL, orginal);
  }

  /**
   * 正实数
   *
   * @param orginal
   * @return boolean
   */
  public static boolean isPositiveRealNumber(String orginal) {
    return isPositiveDecimal(orginal) || isPositiveInteger(orginal);
  }

  /**
   * 计算现品票张数
   *
   * @param devQty，交货数量
   * @param cntQty，捆包数
   * @return
   */
  public static int getTicketNum(float devQtyF, float cntQtyF) {
    BigDecimal devQty = floatToDecimal(devQtyF);

    BigDecimal cntQty = floatToDecimal(cntQtyF);

    if ((devQty.compareTo(cntQty) < 0) || (compareTo(devQty, cntQty) > 0)) {
      return (devQty.divide(cntQty, 3, BigDecimal.ROUND_DOWN)).intValue() + 1;
    } else {
      return (devQty.divide(cntQty, 3, BigDecimal.ROUND_DOWN)).intValue();
    }
  }

  private static int compareTo(BigDecimal devQty, BigDecimal cntQty) {
    return devQty.remainder(cntQty).compareTo(BigDecimal.ZERO);
  }

  /**
   * 计算现品数量
   *
   * @param devQty，交货数量
   * @param cntQty，捆包数
   * @param count，是否是最后一条
   * @return
   */
  public static BigDecimal getTpQty(float devQtyF, float cntQtyF, int count) {
    BigDecimal devQty = floatToDecimal(devQtyF);
    BigDecimal cntQty = floatToDecimal(cntQtyF);

    BigDecimal qty = BigDecimal.ZERO;
    if (devQty.compareTo(cntQty) <= 0) {
      // 交货数量 <= 捆包数时，取交货数量
      qty = devQty;
    } else {
      // 交货数量 > 捆包数时
      if (compareTo(devQty, cntQty) > 0) {
        // 是否是最后一行
        if (count == 1) {
          qty = devQty.remainder(cntQty);
        } else {
          qty = cntQty;
        }
      } else {
        qty = cntQty;
      }
    }
    return qty;
  }

  public static BigDecimal floatToDecimal(float qtyF) {
    BigDecimal qty = new BigDecimal(qtyF);
    qty = qty.divide(BigDecimal.ONE, 3, BigDecimal.ROUND_HALF_UP);
    return qty;
  }

  // 合计数量
  public static BigDecimal add(BigDecimal a, BigDecimal b) {
    if (a == null) {
      a = BigDecimal.ZERO;
    }
    if (b == null) {
      b = BigDecimal.ZERO;
    }

    return a.add(b);
  }

  public static String toString(BigDecimal a) {
    if (a == null)
      return "0";
    return a.toString();
  }

  public static int compare(BigDecimal a, BigDecimal b) {
    if (a == null)
      a = BigDecimal.ZERO;
    if (b == null)
      b = BigDecimal.ZERO;
    return a.compareTo(b);
  }

  public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
    if (a == null)
      a = BigDecimal.ZERO;
    if (b == null)
      b = BigDecimal.ZERO;
    return a.subtract(b);
  }

  public static BigDecimal string2BigDecimal(String qty) {
    if (StringTool.isNull(qty))
      return BigDecimal.ZERO;

    return new BigDecimal(qty.trim());
  }
}
