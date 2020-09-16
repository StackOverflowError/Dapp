/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassThd;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassThdService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 定时任务表管理类
 * @author :lenovo
 * @date :2019年3月28日 上午9:43:58
 */
@Slf4j
@Component("cfgCbassThdManager")
public class CfgCbassThdManager {
	
	/**
	 * 任务容器
	 */
	private Map<String, CfgCbassThd> thdMap=new HashMap<>();
	
	/**
	 * 任务容器
	 */
	private Map<String, CfgCbassThd> thdMapCache=new HashMap<>();
	
	/**
	 * 用于标识配置是否已经初始化完成
	 */
	private volatile boolean isThdMapInited;
	
	@Autowired
	private CfgCbassThdService cfgCbassThdService;
		
	@Autowired
	private SystemManager systemManager;
	
	/**
	 * @Description: 读取定时任务配置
	 * @author lenovo
	 * @throws Exception 
	 * @date 2019年3月28日 下午3:58:18
	 */
	@PostConstruct
	public boolean loadConfig() throws Exception {
		boolean succ=true;
		log.info("-------------------加载定时任务配置  开始----------------------");
		Map<String, CfgCbassThd> thdMapTemp = getCfg();
		synchronized (this) {
			this.thdMapCache.clear();
			this.thdMapCache.putAll(this.getThdMap());
			this.isThdMapInited=false;
			this.thdMap.clear();
			this.thdMap.putAll(thdMapTemp);
			this.isThdMapInited=true;
		}
		
		return succ;

	}
	
	
	/**
	 * @Description: 从数据库获取Task配置
	 * @author lenovo
	 * @date 2019年3月28日 下午4:50:30
	 */
	private Map<String,CfgCbassThd> getCfg() {
		Map<String, CfgCbassThd> tmpMap=new HashMap<String, CfgCbassThd>();
		int count=0;
		QueryWrapper<CfgCbassThd> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("REMOVE_TAG", "1");//在用的
		queryWrapper.like("THD_PROCESS_DOMAIN", systemManager.getIntfSyscode()).or().like("THD_PROCESS_DOMAIN", "PUBLIC");
		List<CfgCbassThd> thdList = cfgCbassThdService.list(queryWrapper);
		if(thdList!=null&&thdList.size()>0) {
			for(CfgCbassThd tdMThdCfg:thdList) {
				if(tdMThdCfg!=null) {
					String thdName = tdMThdCfg.getThdName();
					tmpMap.putIfAbsent(thdName, tdMThdCfg);
					count++;
				}
			}
		}
		log.info("[TdMThdCfgManager]从数据库加载配置----End---"+"---读取配置数="+count);
		return tmpMap;
	}


	/**
	 * @return the thdMap
	 */
	public Map<String, CfgCbassThd> getThdMap() {
		if(isThdMapInited) {
			return thdMap;
		}
		return thdMapCache;
	}



	

	
	
}
