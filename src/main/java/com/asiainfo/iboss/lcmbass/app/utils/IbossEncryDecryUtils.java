package com.asiainfo.iboss.lcmbass.app.utils;
import com.alibaba.druid.filter.config.ConfigTools;

/**
 * <p>
 * Title: IbossEncryDecryUtils.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: 亚信科技
 * </p>
 * 
 * @author asys
 * @date 2018年9月16日
 * @version
 */
public class IbossEncryDecryUtils {

	/**
	 * Druid数据库解密(简单解密)
	 * 
	 * @param passWordCipher 待解密密码
	 * @return 解密后的密码
	 * @throws Exception
	 */
	public static String druidDecry(String passWordCipher) throws Exception {
		return ConfigTools.decrypt(passWordCipher);
	}

	/**
	 * Druid数据库加密(简单加密)
	 * 
	 * @param passWord 待加密明文
	 * @return 加密后的密码
	 * @throws Exception
	 */
	public static String druidEncry(String passWord) throws Exception {
		return ConfigTools.encrypt(passWord);
	}
}
