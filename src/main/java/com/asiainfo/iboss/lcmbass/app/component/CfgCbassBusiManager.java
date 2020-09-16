package com.asiainfo.iboss.lcmbass.app.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassBusiService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("cfgCbassBusiManager")
public class CfgCbassBusiManager {
	
	@Autowired
	private CfgCbassBusiService cfgCbassBusiService;
	
	private Map<String, CfgCbassBusi> map=new HashMap<>();
	
	private Map<String, CfgCbassBusi> mapCache=new HashMap<>();
	
	private AtomicBoolean isInited=new AtomicBoolean(true);
	
	@PostConstruct
	public boolean loadConfig() throws Exception {
		boolean succ = true;
		log.info("-------------------加载定时任务配置  开始----------------------");
		Map<String, CfgCbassBusi> mapTemp = getCfgFromDB();
		synchronized (this) {
			this.mapCache.clear();
			this.mapCache.putAll(map);
			this.isInited.set(false);
			this.map.clear();
			this.map.putAll(mapTemp);
			this.isInited.set(true);
		}
		return succ;
	}

	private Map<String, CfgCbassBusi> getCfgFromDB() {
		Map<String, CfgCbassBusi> tmpMap = new HashMap<>();
		int count=0;
		QueryWrapper<CfgCbassBusi> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("REMOVE_TAG", "1");//在用的
		List<CfgCbassBusi> thdList = cfgCbassBusiService.list(queryWrapper);
		if(thdList!=null&&thdList.size()>0) {
			for(CfgCbassBusi cfgCbassBusi:thdList) {
				if(cfgCbassBusi!=null) {
					String thdName = cfgCbassBusi.getCommondCode();
					tmpMap.putIfAbsent(thdName, cfgCbassBusi);
					count++;
				}
			}
		}
		log.info("[CfgCbassBusiManager]从数据库加载配置----End---"+"---读取配置数="+count);
		return tmpMap;
	}
	
	public Map<String, CfgCbassBusi> getMap() {
		if(this.isInited.get()) {
			return map;
		}
		return mapCache;
	}

	
	public CfgCbassBusi getByKindId(String kindId) {
		return this.getMap().get(kindId);
	}
}
