/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.task;

import org.springframework.beans.factory.annotation.Autowired;

import com.asiainfo.iboss.lcmbass.app.component.SystemManager;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassTask;

import lombok.Data;
/**
 * @Description: 一级BOSS 定时任务超类，需要执行的任务，需要继承该类，并重新run方法
 * @author :lenovo
 * @date :2019年4月23日 上午10:07:15
 */
@Data
public class IbossSupperTask implements Runnable {

	@Autowired
	private SystemManager systemManager;

	private CfgCbassTask tdMTaskCfg;

	@Override
	public void run() {
		
	}
}
