package com.asiainfo.iboss.lcmbass.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * Title: IBossTimeUtils.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: 亚信科技
 * </p>
 * 
 * @author asys
 * @date 2018年4月18日
 * @version
 */
@Slf4j
public class IbossTimeUtils {
	/**
	 * 获取当前8位时间
	 * 
	 * @return String yyyy-MM-dd HH:mm:ss.SSS格式的当前时间
	 */
	public static String getCurrent22Date() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(S_Date_Format_22);
		return sdf.format(d);
	}
	
	/**
	 * 获取指定格式的时间字符串，如果date为空，返回当前时间
	 * */
	public static String getDateStrFromat(Date date,String dateFormat) {
		if(date==null) {
			return getCurrentDate(dateFormat);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}
	
	/**
	 * 获取当前14位时间
	 * 
	 * @return String yyyyMMddHHmmss格式的当前时间
	 */
	public static String getCurrent14Date() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(S_Date_Format_14);
		return sdf.format(d);
	}

	/**
	 * 获取当前8位时间
	 * 
	 * @return String yyyyMMdd格式的当前时间
	 */
	public static String getCurrent8Date() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(S_Date_Format_8);
		return sdf.format(d);
	}
	/**
	 * @Description: 获取当天结束时间
	 * @return
	 */
	public static Date getTodayEndTime() {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}

	/**
	 * 获取当前时间
	 * 
	 * @param String
	 *            dateFormat yyyy-MM-dd HH:mm:ss yyyyMMddHHmmssSSS yyyyMMddHHmmss
	 *            yyyyMMdd yyyy-MM-dd yyyy/MM/dd EEE MMM dd HH:mm:ss z yyyy
	 * @return String 指定格式的当前时间
	 */
	public static String getCurrentDate(String dateFormat) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(d);
	}

	/**
	 * 将输入的时间字符串转换为时间，可以转换的时间格式为： yyyy-MM-dd HH:mm:ss yyyyMMddHHmmssSSS
	 * yyyyMMddHHmmss yyyyMMdd yyyy-MM-dd yyyy/MM/dd EEE MMM dd HH:mm:ss z yyyy
	 * yyyy-MM-dd HH:mm:ss.ff
	 * 
	 * @param dateStr
	 *            需要被转换的日期字符串
	 * @author fuyc
	 */
	public static Date dateStr2Date(String dateStr) throws Exception {
		Date d = null;
		if (StringUtils.isBlank(dateStr)) {
			throw new Exception("输入的日期为空，请输入正确的时间字符串");
		}
		if (dateStr.contains("-") && dateStr.contains(":") && dateStr.length() == 19) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 14) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 17) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 8) {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 10 && dateStr.contains("-")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 10 && dateStr.contains("/")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 28 && dateStr.contains(":") && dateStr.contains(" ")) {
			SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			d = format.parse(dateStr);
		} else if (dateStr.contains(".") && dateStr.contains("-") && dateStr.contains(":") && dateStr.contains(" ")
				&& (dateStr.length() >= 21 && dateStr.length() <= 23)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			d = format.parse(dateStr);
		} else {
			throw new Exception("需要转换的时间不符合要求格式，请核查后重试！");
		}
		return d;
	}

	/**
	 * @Description:将输入的时间字符串转换为时间
	 * @param dateStr 需要被转换的日期字符串
	 * 可以转换的时间格式为： yyyy-MM-dd HH:mm:ss yyyyMMddHHmmssSSS
	 * yyyyMMddHHmmss yyyyMMdd yyyy-MM-dd yyyy/MM/dd EEE MMM dd HH:mm:ss z yyyy
	 * yyyy-MM-dd HH:mm:ss.ff
	 * @return 
	 * 		转换其他格式数据或出错时返回null
	 * @author asys
	 * @date 2019年5月16日 下午4:09:31
	 */
	public static Date dateStr2DateNoException(String dateStr){
		Date d = null;
		if (StringUtils.isBlank(dateStr)) {
			log.debug("输入的日期为空,无法转换！");
			return d;
		}
		try {
			if (dateStr.contains("-") && dateStr.contains(":") && dateStr.length() == 19) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 14) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 17) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 8) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 10 && dateStr.contains("-")) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 10 && dateStr.contains("/")) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 28 && dateStr.contains(":") && dateStr.contains(" ")) {
				SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
				d = format.parse(dateStr);
			} else if (dateStr.contains(".") && dateStr.contains("-") && dateStr.contains(":") && dateStr.contains(" ")
					&& (dateStr.length() >= 21 && dateStr.length() <= 23)) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				d = format.parse(dateStr);
			} else {
				log.error("需要转换的时间不符合要求格式，请核查后重试！");
			}
		} catch (Exception e) {
			log.error("转换时间出错！--->"+e);
		}	
		return d;
	}

	
	/**
	  * 将输入的时间字符串转换为时间，可以转换的时间格式为，不能识别格式返回defDate
	 *  yyyy-MM-dd HH:mm:ss
	 *  yyyyMMddHHmmssSSS
	 * yyyyMMddHHmmss 
	 * yyyyMMdd 
	 * yyyy-MM-dd 
	 * yyyy/MM/dd EEE MMM dd HH:mm:ss z yyyy
	 * yyyy-MM-dd HH:mm:ss.ff
	 * @param dateStr
	   *            需要被转换的日期字符串
	 * @param defDate 默认值
	 * @author fuyc
	 */
	public static Date dateStr2DateDef(String dateStr,Date defDate){
		Date d = null;
		if (StringUtils.isBlank(dateStr)) {
			return defDate;
		}
		try {
			if (dateStr.contains("-") && dateStr.contains(":") && dateStr.length() == 19) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 14) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 17) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 8) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 10 && dateStr.contains("-")) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 10 && dateStr.contains("/")) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				d = format.parse(dateStr);
			} else if (dateStr.length() == 28 && dateStr.contains(":") && dateStr.contains(" ")) {
				SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
				d = format.parse(dateStr);
			} else if (dateStr.contains(".") && dateStr.contains("-") && dateStr.contains(":") && dateStr.contains(" ")
					&& (dateStr.length() >= 21 && dateStr.length() <= 23)) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				d = format.parse(dateStr);
			} else {
				throw new Exception("需要转换的时间不符合要求格式，请核查后重试！");
			}
		} catch (Exception e) {
			d=defDate;
		}
		
		return d;
	}

	/**
	 * 将输入的时间字符串转换为特定格式的时间字符串，可以转换的时间格式为： yyyy-MM-dd HH:mm:ss yyyyMMddHHmmssSSS
	 * yyyyMMddHHmmss yyyyMMdd yyyy-MM-dd yyyy/MM/dd EEE MMM dd HH:mm:ss z yyyy
	 * yyyy-MM-dd HH:mm:ss.ff
	 * 
	 * @param dateStr
	 *            需要被转换的日期字符串
	 * @param dateFormat
	 *            需要转成的目标格式
	 * @author fuyc
	 */
	public static String convertDateFormat(String dateStr, String dateFormat) throws Exception {
		Date d = null;
		if (StringUtils.isBlank(dateStr)) {
			return dateStr;
		}
		if (StringUtils.isBlank(dateFormat)) {
			throw new Exception("目标格式未输入，请输入目标格式!");
		}
		if (dateStr.contains("-") && dateStr.contains(":") && dateStr.length() == 19) {
			SimpleDateFormat format = new SimpleDateFormat(S_Date_Format_19);
			d = format.parse(dateStr);
		} else if (dateStr.length() == 14) {
			SimpleDateFormat format = new SimpleDateFormat(S_Date_Format_14);
			d = format.parse(dateStr);
		} else if (dateStr.length() == 17) {
			SimpleDateFormat format = new SimpleDateFormat(S_Date_Format_17);
			d = format.parse(dateStr);
		} else if (dateStr.length() == 8) {
			SimpleDateFormat format = new SimpleDateFormat(S_Date_Format_8);
			d = format.parse(dateStr);
		} else if (dateStr.length() == 10 && dateStr.contains("-")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 10 && dateStr.contains("/")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
			d = format.parse(dateStr);
		} else if (dateStr.length() == 28 && dateStr.contains(":") && dateStr.contains(" ")) {
			SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			d = format.parse(dateStr);
		} else if (dateStr.contains(".") && dateStr.contains("-") && dateStr.contains(":") && dateStr.contains(" ")
				&& (dateStr.length() >= 21 && dateStr.length() <= 23)) {
			SimpleDateFormat format = new SimpleDateFormat(S_Date_Format_22);
			d = format.parse(dateStr);
		} else {
			throw new Exception("需要转换的时间不符合要求格式，请核查后重试！");
		}

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(d);
	}

	/**
	 * yyyyMMdd
	 */
	public final static String S_Date_Format_8 = "yyyyMMdd";

	/**
	 * yyyyMMddHHmmss
	 */
	public final static String S_Date_Format_14 = "yyyyMMddHHmmss";

	/**
	 * yyyyMMddHHmmssSSS
	 */
	public final static String S_Date_Format_17 = "yyyyMMddHHmmssSSS";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public final static String S_Date_Format_19 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * yyyyMMddHHmmssSSSSSS
	 */
	public final static String S_Date_Format_20 = "yyyyMMddHHmmssSSSSSS";

	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 */
	public final static String S_Date_Format_22 = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public static int getCurrentDay() {
		SimpleDateFormat sd=new SimpleDateFormat("dd"); 
		Date date=new Date(); 
		return Integer.parseInt(sd.format(date));
	}
	
	public static int getCurrentMonth() {
		SimpleDateFormat sd=new SimpleDateFormat("MM"); 
		Date date=new Date(); 
		return Integer.parseInt(sd.format(date));
	}
	
	

}
