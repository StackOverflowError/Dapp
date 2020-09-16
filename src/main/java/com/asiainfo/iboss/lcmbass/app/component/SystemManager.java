package com.asiainfo.iboss.lcmbass.app.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * Title: SystemManager.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company: 亚信科技
 * </p>
 * @author asys
 * @date 2018年5月8日
 * @version
 */
@Component("systemManager")
@Slf4j
public class SystemManager {

	public final static String S_syscode = "INTF_SYSCODE";

	public final static String S_port = "jetty.port";

	public final static String S_intfWlsHome = "INTF_WLS_HOME";

	public final static String S_isTest = "IS_TEST";
		
	public final static String S_logPath="LOG_PATH";
	
	public final static String S_sysEnvironment="spring.profiles.active";

	private Map<String, String> systemMap = new HashMap<String, String>();

	@PostConstruct
	public void init() throws Exception {
		String intfWlsHome=System.getProperty(S_intfWlsHome);
		if(StringUtils.isBlank(intfWlsHome)) {
			log.error("请配置INTF_WLS_HOME(工作路径，用于存放生成的自定义文件等)路径，请加入启动参数-DINTF_WLS_HOME=/home/xxx/xxx...;【特别注意，请不要在末尾加路径分割符！】");
			throw new Exception("请配置INTF_WLS_HOME(工作路径，用于存放生成的自定义文件等)路径，请加入启动参数-DINTF_WLS_HOME=/home/xxx/xxx...;【特别注意，请不要在末尾加路径分割符！】");
		}
		if(StringUtils.endsWith(intfWlsHome, "/")||StringUtils.endsWith(intfWlsHome, "\\")) {
			getSystemMap().put(S_intfWlsHome, StringUtils.substring(intfWlsHome, 0, intfWlsHome.length()-1));
		}
		String sysCode=System.getProperty(S_syscode);
		if(StringUtils.isBlank(sysCode)) {
			log.error("请配置系统编码INTF_SYSCODE(唯一标识服务)，请加入启动参数-DINTF_SYSCODE=xxxxx");
			throw new Exception("请配置系统编码INTF_SYSCODE(唯一标识服务)，请加入启动参数-DINTF_SYSCODE=xxxxx");
		}
		
		String logPath=System.getProperty(S_logPath);
		if(StringUtils.isBlank(logPath)) {
			log.error("请配置日志目录LOG_PATH(日志存放路径)，请加入启动参数-DLOG_PATH=/home/xxx/xxx...【特别注意，请不要在末尾加路径分割符！】");
			throw new Exception("请配置日志目录LOG_PATH(日志存放路径)，请加入启动参数-DLOG_PATH=/home/xxx/xxx...【特别注意，请不要在末尾加路径分割符！】");
		}
		
		Properties properties = System.getProperties();
		for (Object key : properties.keySet()) {
			Object value = properties.get(key);
			if(value==null) {
				getSystemMap().put(key + "", "");
				continue;
			}
			getSystemMap().put(key + "", value + "");
		}
	}
	
	/**
	 * 获取环境
	 */
	public String getSysEnvironment() {
		return getSysParamVlaue(S_sysEnvironment);
	}

	/**
	 * 获取测试标识，如果本地测试，可以设置为true,可以使用redis里面的配置代替Oracle中的配置
	 */
	public String getIsTest() {
		return getSysParamVlaue(S_isTest);
	}

	/**
	 * 获取服务器根路径 比如：/home/webapp/W_IBSA/webapps
	 */
	public String getIntfWlsHome() {		
		return getSysParamVlaue(S_intfWlsHome);
	}

	/**
	 * 获取SYSCODE 比如W_IBSA/W_IGNA...
	 */
	public String getIntfSyscode() {
		return getSysParamVlaue(S_syscode);
	}
	

	/**
	 * 根据key获取系统参数
	 * @param String  key 参数键
	 * @return value 系统参数
	 */
	public String getSysParamVlaue(String key) {
		return getSystemMap().get(key);
	}

	public Map<String, String> getSystemMap() {
		return systemMap;
	}

	public void setSystemMap(Map<String, String> systemMap) {
		this.systemMap = systemMap;
	}
	
}
