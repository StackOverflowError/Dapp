/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 老的IBOSS的Spring类，不建议使用，但是不想改了，先用着吧，后面看心情改
 * @author :lenovo
 * @date :2019年3月12日 下午3:19:01
 */
@Slf4j
@Deprecated
public class OldIbossStringTools {
	/**
	 * 字符串是否有效(null,""时无效)
	 * 
	 * @param str
	 *            String 字符串
	 * @return boolean 是否有效
	 */
	public static boolean isValid(String str) {
		if (str == null || "".equals(str))
			return false;
		else
			return true;
	}

	/**
	 * 判断字符串是否有效（null或trim()后为""则无效）
	 * <p>
	 * Create/Modify at: 2009-4-9 下午09:53:40
	 * 
	 * @param str
	 *            String 待判断的字符串
	 * @return boolean true-有效，false-无效
	 * @auther wanglj
	 */
	public static boolean isTrimValid(String str) {
		if (str == null || str.trim().equals(""))
			return false;
		else {
			return true;
		}
	}

	/**
	 * 获取字符串的StringRange列表
	 * 
	 * @param sourceStr
	 *            原字符串
	 * @param fromStr
	 *            匹配正则的字符串
	 * @param exceptStr
	 *            排除匹配正则的字符串
	 * @return List 非null的StringRange列表
	 */
	public static List<StringRange> getStringRangeList(String sourceStr, String fromStr, String exceptStr) {
		List<StringRange> rangeList = new ArrayList<StringRange>();
		if (sourceStr == null || fromStr == null || exceptStr == null)
			return rangeList;
		List<StringRange> exceptList = new ArrayList<StringRange>();
		Pattern fromPat = Pattern.compile(fromStr);
		Pattern exceptPat = Pattern.compile(exceptStr);
		Matcher matcher = exceptPat.matcher(sourceStr);
		StringRange range = null;
		int pos = 0;
		while (matcher.find()) {
			range = new StringRange();
			range.setStart(matcher.start());
			range.setEnd(matcher.end());
			exceptList.add(range);
		}
		matcher = fromPat.matcher(sourceStr);
		out: while (matcher.find()) {
			pos = matcher.start();
			for (int i = 0; i < exceptList.size(); i++) {
				range = (StringRange) exceptList.get(i);
				if (pos >= range.getStart() && pos < range.getEnd())
					continue out;
			}
			range = new StringRange();
			range.setStart(matcher.start());
			range.setEnd(matcher.end());
			range.setContent(matcher.group());
			range.setLength(matcher.group().length());
			rangeList.add(range);
		}
		exceptList.clear();
		return rangeList;
	}

	/**
	 * 替换字符串中所有的字符串
	 * 
	 * @param sourceStr
	 *            原字符串
	 * @param fromStr
	 *            被替换的匹配正则字符串
	 * @param toStr
	 *            替换成匹配的字符串
	 * @param exceptStr
	 *            排除匹配正则的字符串
	 * @return String 替换结果字符串
	 */
	public static String replaceAllString(String sourceStr, String fromStr, String toStr, String exceptStr) {
		String retStr = sourceStr;
		if (sourceStr == null || fromStr == null || toStr == null || exceptStr == null)
			return retStr;
		// 这里得到的list一定要是按start从小到大排序，且不重叠
		List<StringRange> rangeList = getStringRangeList(sourceStr, fromStr, exceptStr);
		StringRange range = null;
		StringBuffer sb = new StringBuffer(sourceStr);
		// 倒序替换字符
		for (int i = rangeList.size() - 1; i >= 0; i--) {
			range = (StringRange) rangeList.get(i);
			sb.replace(range.getStart(), range.getEnd(),
					Pattern.compile(fromStr).matcher(range.getContent()).replaceFirst(toStr));
		}
		retStr = sb.toString();
		return retStr;
	}

	/**
	 * 替换字符串中第一个字符串
	 * 
	 * @param sourceStr
	 *            原字符串
	 * @param fromStr
	 *            被替换的匹配正则字符串
	 * @param toStr
	 *            替换成匹配的字符串
	 * @param exceptStr
	 *            排除匹配正则的字符串
	 * @return String 替换结果字符串
	 */
	public static String replaceFirstString(String sourceStr, String fromStr, String toStr, String exceptStr) {
		String retStr = sourceStr;
		if (sourceStr == null || fromStr == null || toStr == null || exceptStr == null)
			return retStr;
		// 这里得到的list一定要是按start从小到大排序，且不重叠
		List<StringRange> rangeList = getStringRangeList(sourceStr, fromStr, exceptStr);
		StringRange range = null;
		StringBuffer sb = new StringBuffer(sourceStr);
		if (rangeList.size() > 0) {
			range = (StringRange) rangeList.get(0);
			sb.replace(range.getStart(), range.getEnd(),
					Pattern.compile(fromStr).matcher(range.getContent()).replaceFirst(toStr));
		}
		retStr = sb.toString();
		return retStr;
	}

	/**
	 * 替换字符串中最后一个字符串
	 * 
	 * @param sourceStr
	 *            原字符串
	 * @param fromStr
	 *            被替换的匹配正则字符串
	 * @param toStr
	 *            替换成匹配的字符串
	 * @param exceptStr
	 *            排除匹配正则的字符串
	 * @return String 替换结果字符串
	 */
	public static String replaceLastString(String sourceStr, String fromStr, String toStr, String exceptStr) {
		String retStr = sourceStr;
		if (sourceStr == null || fromStr == null || toStr == null || exceptStr == null)
			return retStr;
		// 这里得到的list一定要是按start从小到大排序，且不重叠
		List<StringRange> rangeList = getStringRangeList(sourceStr, fromStr, exceptStr);
		StringRange range = null;
		StringBuffer sb = new StringBuffer(sourceStr);
		if (rangeList.size() > 0) {
			range = (StringRange) rangeList.get(rangeList.size() - 1);
			sb.replace(range.getStart(), range.getEnd(),
					Pattern.compile(fromStr).matcher(range.getContent()).replaceFirst(toStr));
		}
		retStr = sb.toString();
		return retStr;
	}

	/**
	 * 当字符串为空时,返回defaultValue,否则返回原字符串
	 * 
	 * @param str
	 *            原字符串
	 * @param defaultValue
	 *            被替换的字符
	 * @return String
	 */
	public static String nvl(String str, String defaultValue) {
		if (str == null) {
			str = defaultValue;
		}
		return str;
	}

	/**
	 * 当字符串为空时,返回defaultValue,否则返回原字符串
	 * 
	 * @param str
	 *            原字符串
	 * @param defaultValue
	 *            被替换的字符
	 * @param level
	 *            0-null,1-null&"",2-null&""&trim""
	 * @return String
	 */
	public static String nvl(String str, String defaultValue, int level) {
		if (str == null) {
			str = defaultValue;
		} else if (level == 1 && "".equals(str)) {
			str = defaultValue;
		} else if (level == 2 && ("".equals(str) || "".equals(str.trim()))) {
			str = defaultValue;
		}
		return str;
	}

	/**
	 * 判断两个字符串是否相等
	 * 
	 * @param str1
	 *            字符串1
	 * @param str2
	 *            字符串2
	 * @return boolean 是否相等
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null && str2 == null || str1 != null && str1.equals(str2))
			return true;
		else
			return false;
	}

	/**
	 * 判断两个字符串trim是否相等
	 * 
	 * @param str1
	 *            字符串1
	 * @param str2
	 *            字符串2
	 * @return boolean 是否相等
	 */
	public static boolean equalsWithTrim(String str1, String str2) {
		if (str1 == null && str2 == null || str1 != null && str1.trim().equals(str2.trim()))
			return true;
		else
			return false;
	}

	/**
	 * 按字节长度得到子字符串(不允许半个字符)
	 * 
	 * @param str
	 *            原字符串
	 * @param length
	 *            字节长度
	 * @return String 子字符串
	 */
	public static String subStringByByteLength(String str, int length) {
		return subStringByByteLength(str, length, false, true);
	}

	public static String subStringByGBKByteLength(String str, int length) {

		if (str == null || length < 0)
			return str;
		byte[] strByte;
		try {
			strByte = str.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			log.error(""+e);
			return "";
		}
		byte[] newByte = null;
		int newlen = 0;
		if (strByte.length > length) {
			for (int i = 0; i < length; i++)
				if (strByte[i] > 0)
					newlen++;
				else {
					// 双字节
					i++;
					if (i < length)
						newlen += 2;
				}
			newByte = new byte[newlen];
			for (int i = 0; i < newlen; i++)
				newByte[i] = strByte[i];
		} else
			newByte = strByte;

		String ret = "";
		try {
			ret = new String(newByte, "GBK");
		} catch (UnsupportedEncodingException e) {
			log.error(""+e);
		}
		return ret;
	}

	/**
	 * 按字节长度得到子字符串
	 * 
	 * @param str
	 *            原字符串
	 * @param length
	 *            字节长度
	 * @param allowHalf
	 *            是否允许半个字符
	 * @param fromHead
	 *            是否从头开始截取,true从头部,false从尾部
	 * @return String 子字符串
	 */
	public static String subStringByByteLength(String str, int length, boolean allowHalf, boolean fromHead) {
		if (str == null || length < 0 || str.getBytes().length <= length)
			return str;
		StringBuffer sb = new StringBuffer();
		byte[] strByte = str.getBytes();
		if (fromHead) {
			sb.append(new String(strByte, 0, length));
			if (!allowHalf) {
				int endPos = Math.min(sb.length() - 1, str.length() - 1);
				while (endPos >= 0 && sb.charAt(endPos) != str.charAt(endPos)) {
					sb.deleteCharAt(endPos);
					endPos--;
				}
			}
		} else {
			sb.append(new String(strByte, strByte.length - length, length));
			if (!allowHalf) {
				int minLength = Math.min(sb.length(), str.length());
				while (minLength >= 1 && sb.charAt(sb.length() - minLength) != str.charAt(str.length() - minLength)) {
					sb.deleteCharAt(sb.length() - minLength);
					minLength--;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 制造重复字符串
	 * 
	 * @param dupStr
	 *            需要重复的字符串
	 * @param splitStr
	 *            分隔符
	 * @param dupTime
	 *            重复次数
	 * @param needEnd
	 *            是否需要分隔符结束
	 * @return String 重复字符串
	 */
	public static String productDupString(String dupStr, String splitStr, int dupTime, boolean needEnd) {
		if (dupTime < 1)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dupTime; i++) {
			sb.append(dupStr);
			if (!needEnd && i != dupTime - 1)
				sb.append(splitStr);
		}
		return sb.toString();
	}

	/**
	 * 格式化字符串,添加空格
	 * 
	 * @param str
	 *            原字符串
	 * @param totalLen
	 *            结果总长
	 * @param isappend
	 *            是否附加,true:加在后面,false:加在前面
	 * @return String 格式化的字符串
	 */
	public static String addBlank(String str, int totalLen, boolean isappend) {
		return addSingleStr(str, " ", totalLen, isappend);
	}

	/**
	 * 格式化字符串,添加单字符
	 * 
	 * @param str
	 *            原字符串
	 * @param sstr
	 *            单字符串
	 * @param totalLen
	 *            结果总长
	 * @param isappend
	 *            是否附加,true:加在后面,false:加在前面
	 * @return String 格式化的字符串
	 */
	public static String addSingleStr(String str, String sstr, int totalLen, boolean isappend) {
		if (str == null)
			str = "null";
		if (sstr == null || sstr.length() != 1)
			return str;
		int len = str.length();
		StringBuffer sb = new StringBuffer();
		if (!isappend)
			sb.append(str);
		for (int i = 0; i < totalLen - len; i++) {
			sb.append(sstr);
		}
		if (isappend)
			sb.append(str);
		return sb.toString();
	}

	/**
	 * 通过标志获取xml中的第一个字符串
	 * 
	 * @param xml
	 *            xml字符串
	 * @param flag
	 *            标志
	 * @return String 获取到的字符串值,非null
	 */
	public static String getFirstXmlStrByFlag(String xml, String flag) {
		if (!isValid(xml) || !isValid(flag))
			return "";
		// String beginFlag = spellXmlName(flag, true);
		String retStr = "";
		StringBuffer sb = new StringBuffer();
		String beginFlagLeft = sb.append("<").append(flag).toString();
		String beginFlagRight = ">";
		int flagBeginPos = xml.indexOf(beginFlagLeft);
		String xmlTemp = "";
		int beginFlagRightPos = 0;
		if (flagBeginPos > -1) {
			xmlTemp = xml.substring(flagBeginPos);
			beginFlagRightPos = xmlTemp.indexOf(beginFlagRight);
		}
		String endFlag = spellXmlName(flag, false);
		int beginPos = beginFlagRightPos + 1;
		int endPos = xmlTemp.indexOf(endFlag);
		if (beginPos > -1 && endPos >= beginPos) {
			retStr = xmlTemp.substring(beginPos, endPos);
		}
		return retStr;
	}

	/**
	 * 通过标志获取xml中的第一个字符串(包含记号)
	 * 
	 * @param xml
	 *            xml字符串
	 * @param flag
	 *            标志
	 * @return String 获取到的字符串值,非null
	 */
	public static String getFirstXmlStrByFlagContainFlag(String xml, String flag) {
		if (!isValid(xml) || !isValid(flag))
			return "";
		StringBuffer sb = new StringBuffer();
		String beginFlagLeft = sb.append("<").append(flag).toString();
		int flagBeginPos = xml.indexOf(beginFlagLeft);
		String endFlag = spellXmlName(flag, false);
		String retStr = "";
		int beginPos = flagBeginPos;
		int endPos = xml.indexOf(endFlag) + endFlag.length();
		if (beginPos > -1 && endPos >= beginPos) {
			retStr = xml.substring(beginPos, endPos);
		}
		return retStr;
	}

	/**
	 * 通过标志设置xml中的第一个字符串
	 * 
	 * @param xml
	 *            xml字符串
	 * @param flag
	 *            标志
	 * @param str
	 *            字符串值
	 * @return String 设置入字符串值的xml
	 */
	public static String setFirstXmlStrByFlag(String xml, String flag, String str) {
		if (!isValid(xml) || !isValid(flag))
			return xml;
		String startFlag = spellXmlName(flag, true);
		String endFlag = spellXmlName(flag, false);
		int startPos = xml.indexOf(startFlag) + startFlag.length();
		int endPos = xml.indexOf(endFlag);
		if (startPos > -1 && endPos >= startPos) {
			xml = new StringBuffer().append(xml.substring(0, startPos)).append(str).append(xml.substring(endPos))
					.toString();
		}
		return xml;
	}

	public static String setFirstXmlStrByFlag1(String xml, String flag, String str) {
		if (!isValid(xml) || !isValid(flag))
			return xml;
		String startFlag = spellXmlName(flag, true);
		String endFlag = spellXmlName(flag, false);
		log.error("2222222222222222222222222" + startFlag + " " + endFlag);
		int startPos = xml.indexOf(startFlag) + startFlag.length();
		int endPos = xml.indexOf(endFlag);
		if (startPos > -1 && endPos >= startPos) {
			xml = new StringBuffer().append(xml.substring(0, startPos)).append(str).append(xml.substring(endPos))
					.toString();
		}
		return xml;
	}

	/**
	 * 拼写xml名
	 * 
	 * @param flag
	 *            标志
	 * @param isBegin
	 *            是否是开始节点
	 * @return String xml名
	 */
	public static String spellXmlName(String flag, boolean isBegin) {
		flag = nvl(flag, "");
		StringBuffer sb = new StringBuffer();
		if (isBegin)
			sb.append("<").append(flag).append(">");
		else
			sb.append("</").append(flag).append(">");
		return sb.toString();
	}

	/**
	 * decode函数,参数数目为0时返回null,参数数目小于3时返回第一个参数,默认返回null,其余同oracle中decode
	 * 
	 * @param strs
	 *            字符串
	 * @return
	 */
	public static String decode(String... strs) {
		if (strs == null || strs.length == 0)
			return null;
		if (strs.length < 3)
			return strs[0];
		String sourceStr = strs[0];
		String retStr = null;
		if (strs.length % 2 == 0) {
			retStr = strs[strs.length - 1];
		}
		for (int i = 1; i + 2 <= strs.length; i = i + 2) {
			if (OldIbossStringTools.equals(sourceStr, strs[i])) {
				retStr = strs[i + 1];
				break;
			}
		}
		return retStr;
	}

	/**
	 * 通过正则表达式获取第一个匹配的字符串
	 * 
	 * @param str
	 *            原字符串
	 * @param regex
	 *            正则表达式
	 * @return 匹配到的字符串
	 */
	public static String getFirstStrByregex(String str, String regex) {
		if (!OldIbossStringTools.isValid(str) || !OldIbossStringTools.isValid(regex))
			return str;
		String retStr = null;
		Matcher matcher = Pattern.compile(regex).matcher(str);
		if (matcher.find()) {
			retStr = matcher.group();
		}
		return retStr;
	}

	/**
	 * 用单个StringBuilder将多个对象拼接成单个String，避免连续的"+"操作产生多余的StringBuilder碎片
	 * 
	 * @param objs
	 * @return
	 */
	public static String buildString(Object... objs) {
		if (objs == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (Object o : objs) {
			sb.append(o);
		}
		return sb.toString();
	}

	/**
	 * 任意对象转成定长字符串<br>
	 * 若length大于value的长度，前面不足的位数填指定的值
	 * 若length小于value的长度，将超出的部分覆盖为指定的值（想截取子串请直接使用subString）
	 * 
	 * @param value
	 * @param length
	 * @param fixChar
	 * @return
	 */
	public static String fixLength(Object value, int length, char fixChar) {
		String valueStr = String.valueOf(value);
		int valueLengh = valueStr.length();
		if (length == valueStr.length()) {
			return valueStr;
		} else {
			int longLen, shortLen;
			if (length > valueLengh) {
				longLen = length;
				shortLen = valueLengh;
			} else {
				longLen = valueLengh;
				shortLen = length;
			}
			char[] resultArr = new char[longLen];
			// 1.正序填充fixChar
			int i = 0;
			for (; i < longLen - shortLen; i++) {
				resultArr[i] = fixChar;
			}
			// 2.逆序从valueStr尾部取字符填充剩余部分
			for (int j = 1; j <= shortLen; j++) {
				resultArr[longLen - j] = valueStr.charAt(valueLengh - j);
			}
			return String.valueOf(resultArr);
		}
	}

	/**
	 * 任意对象转成定长字符串<br>
	 * 若length大于value的长度，前面不足的位数填'0'
	 * 若length小于value的长度，将超出的部分覆盖为'0'（想截取子串请直接使用subString）
	 * 
	 * @param value
	 * @param length
	 * @return
	 */
	public static String fixLength(Object value, int length) {
		return fixLength(value, length, '0');
	}

	public static String getFirstSoapStrByFlag(String xml, String key, String value, String svcCont) {
		String[] tmps = new String[2];
		tmps[0] = key;
		tmps[1] = value;
		String before = xml.substring(0, xml.indexOf(tmps[1].substring(4) + ">"));
		String tag = xml.substring(before.lastIndexOf("<") + 1, xml.indexOf(tmps[1].substring(4) + ">") - 1);
		String start = "<" + tag + ":" + tmps[1].substring(4) + ">";
		String end = "</" + tag + ":" + tmps[1].substring(4) + ">";
		String tmpValue = xml.substring(xml.indexOf(start) + start.length(), xml.indexOf(end));
		svcCont = svcCont.substring(0, svcCont.indexOf(start) + start.length()) + tmpValue
				+ svcCont.substring(svcCont.indexOf(end), svcCont.length());
		return svcCont;
	}

	/**
	 * 替换soap报文的svccont S模块
	 */
	public static String getFirstSoapStrByFlagsoap(String xml, String key, String value, String svcCont) {
		String tag = value.split("\\:")[0].split("\\$")[1];
		String start = "<" + tag + ":" + value.split(":")[1] + ">";
		String end = "</" + tag + ":" + value.split(":")[1] + ">";
		String tmpValue = xml.substring(xml.indexOf(start) + start.length(), xml.indexOf(end));
		String fieldValue = ("<" + key + ">");
		String fieldend = ("</" + key + ">");
		
		svcCont = svcCont.substring(0, svcCont.indexOf(fieldValue) + (fieldValue).length()) + tmpValue
				+ svcCont.substring(svcCont.indexOf(fieldend), svcCont.length());
		return svcCont;
	}
}


class StringRange{
	/**
	 * 开始位置
	 */
	private int start = 0;

	/**
	 * 结束位置
	 */
	private int end = 0;

	/**
	 * 字符串长度
	 */
	private int length = 0;

	/**
	 * 字符串内容
	 */
	private String content = "";

	/**
	 * 获取开始位置
	 * 
	 * @return int,开始位置
	 */
	public int getStart() {
		return start;
	}

	/**
	 * 设置开始位置
	 * 
	 * @param start,开始位置
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * 获取字符串内容
	 * 
	 * @return String,字符串内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置字符串内容
	 * 
	 * @param content,字符串内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取结束位置
	 * 
	 * @return int,结束位置
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * 设置结束位置
	 * 
	 * @param end,结束位置
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * 获取字符串长度
	 * 
	 * @return int,字符串长度
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 设置字符串长度
	 * 
	 * @param length,字符串长度
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 覆盖toString方法
	 * 
	 * @return String
	 */
	public String toString() {
		return new StringBuffer().append("start=").append(start).append("|").append("end=").append(end).append("|")
				.append("length=").append(length).append("|").append("content=").append(content).toString();
	}
}
