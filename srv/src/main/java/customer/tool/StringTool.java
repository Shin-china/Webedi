package customer.tool;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.sap.cds.services.request.UserInfo;

import io.micrometer.common.util.StringUtils;

public class StringTool {

	// 判断字符串为空
	public static boolean isNull(String value) {
		if (value == null || "".equals(value)) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String value) {
		if (value == null || "".equals(value)) {
			return true;
		}
		return false;
	}

	public static boolean isNumeric(String str) {
		if (str == null || "".equals(str)) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static BigDecimal checkIsNum(String string) {

		try {
			if (string != null)
				string = string.replaceAll(",", "");
			BigDecimal str = new BigDecimal(string);
			if (str.compareTo(new BigDecimal("0")) > 0) {
				return str;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}

	public static boolean checkIsInt(String string) {

		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public static String CHINESE_REGEX = "[\\u4e00-\\u9fa5]+";

	// 是否为汉字
	public static boolean isChinese(String str) {
		return str.matches(CHINESE_REGEX);
	}

	// 字符串转数字
	public static BigDecimal getBigDecimal(String obj) {
		if (obj == null || "".equals(obj)) {
			return new BigDecimal("0");
		} else {
			return new BigDecimal(obj);
		}
	}

	public static String combine(String s1, String s2, String split) {
		StringBuffer s = new StringBuffer("");
		if (s1 != null) {
			s.append(s1);
			if (s2 != null) {
				s.append(split);
				s.append(s2);
			}

		} else {
			if (s2 != null) {
				s.append(s2);
			}
		}
		return s.toString();
	}

	/*
	 * 
	 * 创 建 人: 功能描述 : 自动截取掉单号前面是0的字符
	 */
	public static String exractNo(String no) {
		if (no != null) {
			StringBuffer sb = new StringBuffer(no);
			int length = sb.length();
			for (int i = 0; i < length; i++) {
				if ("0".equals(sb.substring(0, 1))) {
					sb.delete(0, 1);
				} else {
					break;
				}
			}
			return sb.toString();
		}

		return no;

	}

	/*
	 * 获取参数值**
	 * 
	 * @param jo JSONObject*
	 * 
	 * @param entityName 字段名称*
	 * 
	 * @param str1 用于判断获取字段值为空则返回想要的值，如“-”*@return str
	 */

	public static String getResStr(JSONObject jo, String entityName, String str1) {

		String str = jo.get(entityName) == null ? str1 : jo.get(entityName).toString();

		return str;
	}

	// list 去重
	public static List<String> delRepeat(List<String> list) {
		List<String> myList = list.stream().distinct().collect(Collectors.toList());
		return myList;
	}

	/**
	 * 将0开头的全部去掉
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceFirst(String str) {
		if (StringUtils.isNotBlank(str)) {
			return str.replaceFirst("^0*", "");
		}
		return str;
	}

	public static String InputStream2String(InputStream inputStream) throws IOException {

		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		String str = sb.toString();
		return str;
	}

	/**
	 * 判断字符串是否为中文
	 *
	 * @param input
	 * @return
	 */
	public static boolean isJapanese(String input) {
		if (input.equalsIgnoreCase("J") || input.equalsIgnoreCase("JA") || input.equalsIgnoreCase("k")) {
			return true;
		}
		return false;
	}

	public static String replaceAllTrim(String str) {
		return str.replaceAll("\\s*", "");
	}

	/**
	 * 截取_后的数据
	 * 
	 * @param colorCode
	 * @return
	 */
	public static String subColorCode(String colorCode) {
		if (StringUtils.isNotBlank(colorCode)) {
			if (colorCode.contains("_")) {
				return colorCode.substring(colorCode.lastIndexOf("_") + 1);
			} else {
				return colorCode;
			}
		}
		return "";
	}

	public static String encodeURIComponent(String s) throws UnsupportedEncodingException {
		return s.replaceAll(" ", "%20");
	}

	public static boolean isEq(Object a, Object b) {
		if (a != null && b != null) {
			return a.equals(b);
		}
		return false;
	}

	public static String join(String a, String b) {
		if (a == null)
			a = "";
		if (b == null)
			b = "";
		return a + b;
	}

	public static Object sqlIsnull(Object a, Object b) {
		if (isNull(a.toString()))
			return b;
		return a;
	}

	public static String[] splite(String str, String ch) {
		if ("|".equals(ch)) {
			return str.split("[|]");
		}

		if ("*".equals(ch)) {
			return str.split("\\*");
		}

		if ("\\".equals(ch)) {
			return str.split("\\\\");
		}

		if ("^".equals(ch)) {
			return str.split("\\^");
		}
		if (".".equals(ch)) {
			return str.split("[.]");
		}
		if ("?".equals(ch)) {
			return str.split("[?]");
		}
		if ("[".equals(ch)) {
			return str.split("\\[");
		}

		if ("]".equals(ch)) {
			return str.split("\\]");
		}

		return str.split(ch);

	}

	public static String booealn2String(Boolean a) {
		if (a)
			return "Y";
		else
			return "N";
	}

	public static Boolean String2Boolean(String a) {
		if ("N".equals(a))
			return false;

		return true;
	}

	public static String String2Boolean1(String a) {

		return String.format("%10s", "abc");
	}

}