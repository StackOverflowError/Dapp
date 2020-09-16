/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.component;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.asiainfo.iboss.lcmbass.app.starter.IbossSupperStarter;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>Title: AppHeathManager.java</p>
 * <p>Description: </p>
 * <p>Company: 亚信科技</p> 
 * @author	asys
 * @date	2018年6月27日
 * @version 
 */
@Slf4j
@Component("appHealthManager")
public class AppHealthManager {
	
	
	@Autowired
	private ApplicationContext applicationContext;
	
	
	@PostConstruct
	public void init() {
		this.healthStates="false";
		this.lastBusiTime=0L;
	}
	
	public Long getLastBusiTime() {
		return lastBusiTime;
	}

	public synchronized void setLastBusiTime(Long lastBusiTime) {
		this.lastBusiTime = lastBusiTime;
	}

	public String getHealthStates() {
		return healthStates;
	}

	public synchronized void setHealthStates(String healthStates) throws Exception {
		if(StringUtils.equals(getHealthStates(), healthStates)) {
			log.warn("系统的状态status={},无需再次设置！",healthStates);
			return;
		}
		//开启线程、定时任务等
		Map<String, IbossSupperStarter> starters = applicationContext.getBeansOfType(IbossSupperStarter.class);
		if(starters!=null && starters.size()>0) {
			for(String beanName:starters.keySet()) {
				IbossSupperStarter starter = starters.get(beanName);
				if(StringUtils.equalsIgnoreCase("true", healthStates)) {
					starter.startAll();
				}else {
					starter.stopAll();
				}
				
			}
		}
		
		this.healthStates = healthStates;
	}

	/**
	 * 判断服务是否可以关闭
	 * true 可以关闭
	 * false 不可以关闭，还有交易未处理完
	 * */
	public String getAppCloseEnable() {
		//可以关闭的条件：1、健康状态必须是false;2、最后一笔交易进来后30s
		if(StringUtils.equalsIgnoreCase("false", this.healthStates)) {
			Long currentTime=System.currentTimeMillis();
			if((currentTime-this.lastBusiTime)>=(30*1000L)) {
				return "true";
			}
		}
		return "false";
	}

	/**
	 * 记录最后一笔交易时间
	 * */
	private Long lastBusiTime=0L;
	
	/**
	 * 健康状态 true健康，false不健康
	 * */
	private String healthStates;

}
