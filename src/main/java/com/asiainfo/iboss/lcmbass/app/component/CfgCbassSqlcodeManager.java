package com.asiainfo.iboss.lcmbass.app.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassSqlcode;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassSqlcodeService;
import com.asiainfo.iboss.lcmbass.app.utils.IbossSpringUtils;
import com.asiainfo.iboss.lcmbass.app.utils.OldIbossStringTools;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("cfgCbassSqlcodeManager")
public final class CfgCbassSqlcodeManager {

	@Autowired
	private SystemManager systemManager;
	
	@Autowired
	private CfgCbassSqlcodeService cfgCbassSqlcodeService;

	/**
	 * 用于标识配置是否已经初始化完成
	 */
	private volatile boolean isSqlMapInited;

	/**
	 * sql配置的容器.
	 */
	private Map<String, String> sqlMap = new HashMap<String, String>();

	/**
	 * sql配置的容器中间态
	 */
	private Map<String, String> sqlMapCache = new HashMap<String, String>();
	
	

	/**
	 * sql重加载次数
	 */
	private int loadCount = 0;

	/**
	 * sql最后加载时间
	 */
	private Date firstLoadDate = new Date();

	/**
	 * sql最后加载时间
	 */
	private Date lastLoadDate = new Date();

	/**
	 * 获得sql首次加载时间
	 * 
	 * @return Date,首次加载时间
	 */
	public Date getFirstLoadDate() {
		return firstLoadDate;
	}

	/**
	 * 获得sql最后加载时间
	 * 
	 * @return Date,最后加载时间
	 */
	public Date getLastLoadDate() {
		return lastLoadDate;
	}

	/**
	 * 获得sql加载次数
	 * 
	 * @return int,加载次数
	 */
	public int getLoadCount() {
		return loadCount;
	}

	/**
	 * 读取sql,重新读取sql
	 * 
	 * @return boolean 加载是否成功
	 * @throws Exception
	 */
	@PostConstruct
	public boolean loadSql() throws Exception {
		boolean succ = true;
		Map<String, String> tmpMap = null;
		tmpMap = getSqlFromDB();//可能报错
		
		synchronized (this) {
			this.sqlMapCache.clear();
			this.sqlMapCache.putAll(sqlMap);
			this.isSqlMapInited = false;
			this.sqlMap.clear();
			this.sqlMap.putAll(tmpMap);
			this.isSqlMapInited = true;
			loadCount++;
			lastLoadDate = new Date();
		}
		return succ;
	}

	/**
	 * 从数据库获取配置
	 * @return Map sql配置信息
	 * @throws Exception
	 */
	private Map<String, String> getSqlFromDB() throws Exception {
		Map<String, String> tmpMap = new HashMap<String, String>();
		log.info("[SqlManager]从数据库加载配置---Start---" + systemManager.getIntfSyscode());
		//查询PUBLIC的
		QueryWrapper queryWrapper=new QueryWrapper();
		queryWrapper.eq("SYSCODE", "PUBLIC");
		List<CfgCbassSqlcode> publicSqlList=null;
		
		publicSqlList=cfgCbassSqlcodeService.list(queryWrapper);
		

		QueryWrapper queryWrapper2=new QueryWrapper();
		queryWrapper2.eq("SYSCODE", systemManager.getIntfSyscode());
		List<CfgCbassSqlcode> syscodeSqlList=null;
		
		syscodeSqlList=cfgCbassSqlcodeService.list(queryWrapper2);

		
		if(publicSqlList!=null&&publicSqlList.size()>0) {
			for(CfgCbassSqlcode tdMIfsqlcode:publicSqlList) {
				String key=tdMIfsqlcode.getTableName()+"|"+tdMIfsqlcode.getSqlTag();
				String value=tdMIfsqlcode.getSqlStmt();
				value = OldIbossStringTools.replaceAllString(value, "<!(.|[\\r\\n])*?>", "", "'[^']*'");
				tmpMap.put(key, value);
			}
		}
		if(syscodeSqlList!=null&&syscodeSqlList.size()>0) {
			for(CfgCbassSqlcode tdMIfsqlcode:syscodeSqlList) {
				String key=tdMIfsqlcode.getTableName()+"|"+tdMIfsqlcode.getSqlTag();
				String value=tdMIfsqlcode.getSqlStmt();
				value = OldIbossStringTools.replaceAllString(value, "<!(.|[\\r\\n])*?>", "", "'[^']*'");
				//把PUBLIC的替换掉
				tmpMap.put(key, value);
			}
		}
		int dbCount=0;
		if(publicSqlList != null) {
			dbCount+=publicSqlList.size();
		}
		if(syscodeSqlList != null) {
			dbCount+=syscodeSqlList.size();
		}
		
		log.info("[SqlManager]从数据库加载配置---End---" + systemManager.getIntfSyscode() + "---读取数=" + dbCount + "---配置数="+ tmpMap.size());
		return tmpMap;
	}

	/**
	 * 获取sql语句
	 * 
	 * @param tableName
	 *            表名
	 * @param sqlTag
	 *            sql标记
	 * @return String sql语句
	 */
	public String getSqlStmt(String tableName, String sqlTag) {
		Map<String, String> tmpMap = getSqlMap();
		String sql = (String) tmpMap.get(tableName + "|" + sqlTag);
		sql = sql == null ? "" : sql;
		return sql;
	}

	/**
	 * 获取sql语句
	 * 
	 * @param key
	 *            表名|sql标记
	 * @return String sql语句
	 */
	public String getSqlStmt(String key) {
		Map<String, String> tmpMap = getSqlMap();
		String sql = (String) tmpMap.get(key);
		sql = sql == null ? "" : sql;
		return sql;
	}

	/**
	 * 返回指定表名的所有sqltag 如果查询失败则返回0长度的String[]
	 * 
	 * @param tableName
	 * @return String[] 指定表名的所有sqltag,如果查询失败则返回0长度的String[]
	 */
	public String[] getSqlTagsBytableName(String tableName) {
		Map<String, String> tmpMap = getSqlMap();
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> keys = tmpMap.keySet().iterator();

		String name = "";
		while (keys.hasNext()) {
			name = keys.next();
			if (name != null && name.startsWith(tableName + "|")) {
				list.add(name.substring(tableName.length() + 1));
			}
		}
		
		//排序
		Collections.sort(list,new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String s1 = o1.toLowerCase();
				String s2 = o2.toLowerCase();
				return s1.compareTo(s2);
			}
		});
		
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * 打印所有的sql信息
	 * 
	 * @return String sql信息字符串
	 */
	public String showAllSqls() {
		Map<String, String> tmpMap = getSqlMap();
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for (String key : tmpMap.keySet()) {
			String value = tmpMap.get(key);
			sb.append(key).append("=").append("<").append(value).append(">");
			if (count < tmpMap.size()) {
				sb.append("\n");
				count++;
			}
		}
		return sb.toString();
	}

	/**
	 * 直接获取sql语句
	 * 
	 * @param tableName
	 *            表名
	 * @param sqlTag
	 *            sql标记
	 * @return String sql语句
	 */
	public static String getSqlStmtDirect(String tableName, String sqlTag) {
		CfgCbassSqlcodeManager tdMIfsqlCodeManager=IbossSpringUtils.getBean("cfgCbassSqlcodeManager");
		return tdMIfsqlCodeManager.getSqlStmt(tableName, sqlTag);
	}

	/**
	 * @return the sqlMap
	 */
	public Map<String, String> getSqlMap() {
		if (isSqlMapInited) {
			return sqlMap;
		}
		return sqlMapCache;

	}
}
