package com.asiainfo.iboss.lcmbass.app.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassParam;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassParamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import lombok.extern.slf4j.Slf4j;

/**
 * <p>Title: CfgCbassParamManager.java</p>
 * <p>Description: </p>
 * <p>Company: 亚信科技</p> 
 * @author	asys
 * @date	2018年7月24日
 * @version 
 */
@Slf4j
@Component("cfgCbassParamManager")
public class CfgCbassParamManager {
	
	@Autowired
	private SystemManager systemManager;
	
	@Autowired
	private CfgCbassParamService cfgCbassParamService;
		
	/**
	 * 公共系统代码
	 */
	public static final String PUBLIC_INTF_SYSCODE = "PUBLIC";
	
	/**
	 * 配置项的容器.
	 */
	private Map<String, String[]> paramMap = new HashMap<String, String[]>();

	/**
	 * 配置项的容器.
	 */
	private Map<String, String[]> paramMapCache = new HashMap<String, String[]>();

	/**
	 * 用于标识配置是否已经初始化完成
	 */
	private volatile boolean isParamMapInited;
	
	/**
	 * 读取配置,重新读取配置
	 * @return boolean 读取参数是否成功
	 * @throws Exception
	 */
	@PostConstruct
	public boolean loadConfig() throws Exception {
		boolean succ = true;
		Map<String, String[]> paramMapTemp = getConfigFromDB();
		synchronized (this) {
			this.paramMapCache.clear();
			this.paramMapCache.putAll(paramMap);
			this.isParamMapInited = false;
			this.paramMap.clear();
			this.paramMap.putAll(paramMapTemp);
			this.isParamMapInited = true;
		}
		return succ;
	}

	/**
	 * 从数据库获取配置
	 * @return Map 配置信息
	 * @throws Exception
	 */
	private Map<String, String[]> getConfigFromDB() throws Exception {
		Map<String, String[]> tmpMap = new HashMap<String, String[]>();
		log.info("[ConfigManager]从数据库加载配置---Start---" + systemManager.getIntfSyscode());
		int dbCount = 0;
		int configCount = 0;
		String sysCode = null;
		String lastSysCode = null;
		String configName = null;
		String paraName = null;
		String paraValue = null;
		String key = null;
		String lastKey = null;
		List<String> valueList = new ArrayList<String>();
		String[] paraValueArray = null;
		List<CfgCbassParam> configList = listSystemConfig();
		if(configList!=null&&configList.size()>0) {
			for(CfgCbassParam tdMIfconfig:configList) {
				sysCode=tdMIfconfig.getSyscode();
				configName=tdMIfconfig.getConfigName();
				paraName=tdMIfconfig.getParamName();
				paraValue=tdMIfconfig.getParamValue();
				//存储方式的缘故,ConfigName中不能出现"|"
				key=configName + "|" + paraName;
				if (lastKey == null) {
					valueList.add(paraValue);
					lastSysCode = sysCode;
				} else if (lastKey.equals(key)) {
					if (StringUtils.equals(sysCode, lastSysCode)) {
						addStrListPlus(valueList, paraValue);
					} else if (!PUBLIC_INTF_SYSCODE.equals(sysCode)) {
						valueList.clear();
						addStrListPlus(valueList, paraValue);
						lastSysCode = sysCode;
					}
				} else {
					paraValueArray = (String[]) valueList.toArray(new String[0]);
					tmpMap.put(lastKey, paraValueArray);
					configCount += valueList.size();
					valueList.clear();
					addStrListPlus(valueList, paraValue);
					lastSysCode = sysCode;
				}
				lastKey = key;
				dbCount++;
			}
			if (valueList.size() > 0) {
				paraValueArray = (String[]) valueList.toArray(new String[0]);
				tmpMap.put(lastKey, paraValueArray);
				configCount += valueList.size();
				valueList.clear();
			}
		}
		log.info("[ConfigManager] 从数据库加载配置---End---" + systemManager.getIntfSyscode() + "---读取数=" + dbCount + "---配置数=" + configCount);
		return tmpMap;
	}

	/**
	 * 将字符串加入到list
	 * @param list
	 *            List
	 * @param str
	 *            加入list的字符串
	 * @return boolean,add时true,join时false
	 */
	private boolean addStrListPlus(List<String> list, String str) {
		String lastStr = null;
		StringBuffer sb = new StringBuffer();
		boolean isAdd = true;
		if (list == null) {
			isAdd = false;
		} else if (list.size() == 0) {
			list.add(str);
		} else {
			lastStr = (String) list.get(list.size() - 1);
			if (lastStr != null && str != null && lastStr.endsWith("-->") && str.startsWith("<--")) {
				sb.append(lastStr).append(str).delete(lastStr.length() - 3, lastStr.length() + 3);
				list.set(list.size() - 1, sb.toString());
				sb.delete(0, sb.length());
				isAdd = false;
			} else {
				list.add(str);
			}
		}
		return isAdd;
	}

	/**
	 * 此方法返回由configName和paramName确定的配置项参数的第一个值，注意如果配置项配置了多个参数,返回第一个值.
	 * @param configName 配置项名字
	 * @param paramName 配置项的参数名
	 * @return 返回的配置参数的值.如果查找相应的失败返回""
	 */
	public String getParamValue(String configName, String paramName) {
		Map<String, String[]> tmpMap = getParamMap();
		String[] rtn = (String[]) tmpMap.get(configName + "|" + paramName);
		if (rtn == null || rtn.length == 0 || rtn[0] == null) {
			return "";
		} else {
			return rtn[0];
		}
	}
	
	/**
	 * 此方法返回由configName和paramName确定的配置项参数的第一个值，注意如果配置项配置了多个参数,返回第一个值.
	 * @param configName 配置项名字
	 * @param paramName 配置项的参数名
	 * @param def 默认返回的字符串
	 * @return 返回的配置参数的值.如果查找相应的失败返回默认Def
	 */
	public String getParamValueDef(String configName, String paramName ,String def) {
		
		String paramValue=getParamValue(configName, paramName);
		if(StringUtils.isNotBlank(paramValue)) {
			return paramValue;
		}
		return def;
	}

	/**
	 * 此方法放回由configName和paramName确定的配置项参数的所有值
	 * @param configName
	 *            配置项名字
	 * @param paramName
	 *            配置项的参数名
	 * @return String[] 返回配置参数的所有值,如果查询失败则返回0长度的String[]
	 */
	public String[] getParamValues(String configName, String paramName) {
		Map<String, String[]> tmpMap = getParamMap();
		String[] rtn = (String[]) tmpMap.get(configName + "|" + paramName);
		if (rtn == null) {
			return new String[0];
		} else {
			return rtn;
		}
	}

	/**
	 * 返回指定配置项的所有配置参数名
	 * @param configName
	 *            配置项
	 * @return 有配置参数名，如果查询失败则返回0长度的String[]
	 */
	public String[] getParamNamesByConfigName(String configName) {
		Map<String, String[]> tmpMap = getParamMap();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = tmpMap.keySet().iterator();
		String key = "";
		while (it.hasNext()) {
			key = it.next();
			if (key != null && key.startsWith(configName + "|")) {
				list.add(key.substring(configName.length() + 1));
			}
		}
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String s1 =  o1.toLowerCase();
				String s2 = o2.toLowerCase();
				return s1.compareTo(s2);
			}
		});
		return list.toArray(new String[0]);
	}

	
	
	/**
	 * 返回指定配置项的所有配置参数值
	 * @param configName
	 *            配置项
	 * @return 有配置参数值,如果查询失败则返回0长度的String[][]
	 */
	public String[][] getParamValuesByConfigName(String configName) {
		Map<String, String[]> tmpMap = getParamMap();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = tmpMap.keySet().iterator();
		String key = "";
		while (it.hasNext()) {
			key = it.next();
			if (key != null && key.startsWith(configName + "|")) {
				list.add(key.substring(configName.length() + 1));
			}
		}
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String s1 =  o1.toLowerCase();
				String s2 = o2.toLowerCase();
				return s1.compareTo(s2);
			}
		});
		String[] paramValues = null;
		String paramName = null;
		String[][] rtnValues = new String[list.size()][0];
		for (int i = 0; i < list.size(); i++) {
			paramName = (String) list.get(i);
			paramValues = (String[]) tmpMap.get(configName + "|" + paramName);
			paramValues = paramValues == null ? new String[0] : paramValues;
		}
		return rtnValues;
	}

	/**
	 * 打印所有的参数配置项信息
	 * @return String 配置信息字符串
	 */
	public String showAllParams() {
		Map<String, String[]> tmpMap = getParamMap();
		Set<String> keySet = tmpMap.keySet();
		Iterator<String> it = keySet.iterator();
		StringBuffer sb = new StringBuffer();
		String key = null;
		String[] paramValues = null;
		while (it.hasNext()) {
			key = it.next();
			paramValues = (String[]) tmpMap.get(key);
			sb.append(key).append("=");
			for (int i = 0; i < paramValues.length; i++) {
				sb.append("<").append(paramValues[i]).append(">");
			}
			if (it.hasNext()) {
				sb.append("\n");
			}
				
		}
		return sb.toString();
	}
	
	/**
	 * @return the paramMap
	 */
	public Map<String, String[]> getParamMap() {
		if (isParamMapInited) {
			return paramMap;
		}
		return paramMapCache;
	}

	/**
	 * 获取符合当前系统的配置项
	 *select * from TdMIfconfig t where t.sysCode in ('PUBLIC',?1) order by t.configName,t.paramName,t.sysCode,t.valueSeq
	 */
	@SuppressWarnings("unchecked")
	private List<CfgCbassParam> listSystemConfig(){
		@SuppressWarnings("rawtypes")
		QueryWrapper queryWrapper=new QueryWrapper();
		queryWrapper.in("SYSCODE", Arrays.asList(PUBLIC_INTF_SYSCODE,systemManager.getIntfSyscode()));
		queryWrapper.orderByAsc("CONFIG_NAME","PARAM_NAME","SYSCODE","VALUE_SEQ");
		return  cfgCbassParamService.list(queryWrapper);
	}	
	
}
