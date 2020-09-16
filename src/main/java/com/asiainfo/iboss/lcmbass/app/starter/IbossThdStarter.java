/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.starter;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.asiainfo.iboss.lcmbass.app.component.CfgCbassThdManager;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassThd;
import com.asiainfo.iboss.lcmbass.app.thd.supper.IbossThdSupperCtrl;
import com.asiainfo.iboss.lcmbass.app.utils.IbossSpringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年5月9日 上午10:16:22
 */

@Component("ibossThdStarter")
@Slf4j
public class IbossThdStarter implements IbossSupperStarter{
	
	@Autowired
	private CfgCbassThdManager tdMThdCfgManager;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * @Description: 启动所有的定时任务
	 * @author lenovo
	 * @throws Exception 
	 * @date 2019年3月28日 下午4:00:49
	 */
	@PostConstruct
	public Map<String, CfgCbassThd> startAllThd() throws Exception {
		//先停掉所有线程
		stopAllThd();
		log.info("----------------启动异步线程  开始-----------------------------------");
		//获取所有的线程
		Map<String, CfgCbassThd> thdMap = tdMThdCfgManager.getThdMap();
		if(thdMap!=null && thdMap.size()>0) {
			for(String thdName:thdMap.keySet()) {
				CfgCbassThd tdMThdCfg=thdMap.get(thdName);
				String thdCtrlClass=tdMThdCfg.getThdCtrlClass();
				IbossThdSupperCtrl<?> ctrl=IbossSpringUtils.getBeanByTypeAndName(thdCtrlClass, IbossThdSupperCtrl.class, applicationContext);
				ctrl.setTdMThdCfg(tdMThdCfg);
				ctrl.setThdName(thdName);
				ctrl.startThd();
			}			
		}
		log.info("----------------启动异步线程  完成-----------------------------------");
		return thdMap;
	}
	
	@PreDestroy
	public Map<String, CfgCbassThd> stopAllThd() {
		//获取所有的线程
		log.info("----------------关闭异步线程  开始-----------------------------------");
		Map<String, CfgCbassThd> thdMap = tdMThdCfgManager.getThdMap();
		if(thdMap!=null && thdMap.size()>0) {
			for(String thdName:thdMap.keySet()) {
				CfgCbassThd tdMThdCfg=thdMap.get(thdName);
				String thdCtrlClass=tdMThdCfg.getThdCtrlClass();
				IbossThdSupperCtrl<?> ctrl=IbossSpringUtils.getBeanByTypeAndName(thdCtrlClass, IbossThdSupperCtrl.class, applicationContext);
				ctrl.stopThd();
			}			
		}
		log.info("----------------关闭异步线程  完成-----------------------------------");
		return thdMap;
	}
	
	public CfgCbassThd startThdByName(String thdName) throws Exception {
		CfgCbassThd tdMThdCfg = tdMThdCfgManager.getThdMap().get(thdName);
		String thdCtrlClass=tdMThdCfg.getThdCtrlClass();
		IbossThdSupperCtrl<?> ctrl=IbossSpringUtils.getBeanByTypeAndName(thdCtrlClass, IbossThdSupperCtrl.class, applicationContext);
		ctrl.startThd();
		return tdMThdCfg;
	}
	
	public CfgCbassThd stopThdByName(String thdName) throws Exception {
		CfgCbassThd tdMThdCfg = tdMThdCfgManager.getThdMap().get(thdName);
		String thdCtrlClass=tdMThdCfg.getThdCtrlClass();
		IbossThdSupperCtrl<?> ctrl=IbossSpringUtils.getBeanByTypeAndName(thdCtrlClass, IbossThdSupperCtrl.class, applicationContext);
		ctrl.stopThd();
		return tdMThdCfg;
	}


	@Override
	public void stopAll() throws Exception {
		stopAllThd();
	}


	@Override
	public void startAll() throws Exception {
		startAllThd();
	}
	
}
