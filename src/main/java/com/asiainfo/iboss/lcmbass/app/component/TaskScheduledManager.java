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

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassTask;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassTaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 定时任务表管理类
 * @author :lenovo
 * @date :2019年3月28日 上午9:43:58
 */
@Slf4j
@Component("taskScheduledManager")
public class TaskScheduledManager {
	
	/**
	 * 任务容器
	 */
	private Map<String, CfgCbassTask> scheduledTaskMap=new HashMap<>();
	
	/**
	 * 任务容器
	 */
	private Map<String, CfgCbassTask> scheduledTaskMapCache=new HashMap<>();
	
	/**
	 * 用于标识配置是否已经初始化完成
	 */
	private boolean isscheduledTaskMapInited;
	
	@Autowired
	private CfgCbassTaskService cfgCbassTaskService;
	
	@Autowired
	private SystemManager systemManager;
	
	/**
	 * @Description: 读取定时任务配置
	 * @author lenovo
	 * @throws Exception 
	 * @date 2019年3月28日 下午3:58:18
	 */
	@PostConstruct
	public boolean loadTaskConfig() throws Exception {
		boolean succ=true;
		log.info("-------------------加载定时任务配置  开始----------------------");
		Map<String, CfgCbassTask> taskCfgMapTemp = getTaskCfg();
		synchronized (this) {
			this.scheduledTaskMapCache.clear();
			this.scheduledTaskMapCache.putAll(this.scheduledTaskMap);
			this.isscheduledTaskMapInited=false;
			this.scheduledTaskMap.clear();
			this.scheduledTaskMap.putAll(taskCfgMapTemp);
			this.isscheduledTaskMapInited=true;
		}
		
		return succ;

	}
	
	
	/**
	 * @Description: 从数据库获取Task配置
	 * @author lenovo
	 * @date 2019年3月28日 下午4:50:30
	 */
	private Map<String,CfgCbassTask> getTaskCfg() {
		Map<String, CfgCbassTask> tmpMap=new HashMap<>();
		int count=0;
		QueryWrapper<CfgCbassTask> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("REMOVE_TAG", "1");//在用的
		queryWrapper.like("TASK_PROCESS_DOMAIN", systemManager.getIntfSyscode()).or().like("TASK_PROCESS_DOMAIN", "PUBLIC");
		List<CfgCbassTask> taskList = cfgCbassTaskService.list(queryWrapper);
		if(taskList!=null&&taskList.size()>0) {
			for(CfgCbassTask tdMTaskCfg:taskList) {
				if(tdMTaskCfg!=null) {
					String taskName = tdMTaskCfg.getTaskName();
					tmpMap.putIfAbsent(taskName, tdMTaskCfg);
					count++;
				}
			}
		}
		log.info("[TaskScheduledManager]从数据库加载配置----End---"+"---读取配置数="+count);
		return tmpMap;
	}


	/**
	 * @return the scheduledTaskMap
	 */
	public Map<String, CfgCbassTask> getScheduledTaskMap() {
		if(isscheduledTaskMapInited) {
			return scheduledTaskMap;
		}
		return scheduledTaskMapCache;
	}

	
	
}
