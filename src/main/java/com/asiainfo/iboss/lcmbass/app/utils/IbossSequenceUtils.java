package com.asiainfo.iboss.lcmbass.app.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Title: IbossDeceptiveSequenceUtils.java
 * </p>
 * <p>
 * Description:序列生成工具
 * </p>
 * <p>
 * Company: 亚信科技
 * </p>
 * 
 * @author asys
 * @date 2020年9月7日
 * @version
 */
public class IbossSequenceUtils {
	
	private static final AtomicInteger SEQUENCE=new AtomicInteger(1);


	/**
	 * 不支持分布式系统，每次启动从1开始
	 * */
	public static String get8Sequence() {
		return String.format("%010d", SEQUENCE.get());
	}
	
}
