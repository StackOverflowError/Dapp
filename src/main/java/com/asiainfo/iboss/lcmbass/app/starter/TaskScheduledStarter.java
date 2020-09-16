/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.starter;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.asiainfo.iboss.lcmbass.app.component.SystemManager;
import com.asiainfo.iboss.lcmbass.app.component.TaskScheduledManager;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassTask;
import com.asiainfo.iboss.lcmbass.app.task.IbossSupperTask;
import com.asiainfo.iboss.lcmbass.app.utils.IbossSpringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 定时任务管理类，只能所有的任务停止，所有的启动
 * @author :lenovo
 * @date :2019年3月28日 上午9:43:58
 */

@Slf4j
@Component("taskScheduledStarter")
@DependsOn(value= {"taskScheduledManager","systemManager"})
public class TaskScheduledStarter implements IbossSupperStarter{
	
	@Autowired
	@Qualifier("taskScheduledManager")
	private TaskScheduledManager taskScheduledManager;
	
	@Autowired
	@Qualifier("systemManager")
	private SystemManager systemManager;
	
	@Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	
	@Autowired
	private ApplicationContext applicationContext;
	
	
	/**
	 * @Description: 启动所有的定时任务
	 * @author lenovo
	 * @throws Exception 
	 * @date 2019年3月28日 下午4:00:49
	 */
	@PostConstruct
	public void startAllTask() throws Exception {
		stopAllTask();
        threadPoolTaskScheduler.initialize();
        
        //获取所有实现类Runable接口的对象
        Map<String, Runnable> runableBean = applicationContext.getBeansOfType(Runnable.class);
        log.debug("runableBean------>"+runableBean);
        
		Map<String, CfgCbassTask> scheduledTaskMap = taskScheduledManager.getScheduledTaskMap();
		
		if(scheduledTaskMap==null||scheduledTaskMap.size()==0) {
			log.info("-------------------加载定时任务配置 完成，合计0条数据----------------------");
			return;
		}
		log.info("-------------------加载定时任务配置 完成，合计"+scheduledTaskMap.size()+"条数据----------------------");
        log.info("------------------启动定时任务  开始----------------------");
        for(String key:scheduledTaskMap.keySet()) {
        	CfgCbassTask tdMTaskCfg = scheduledTaskMap.get(key);
        	String taskProcessClass = tdMTaskCfg.getTaskProcessClass();
        	String taskCronExp = tdMTaskCfg.getTaskCronExp();
        	
        	IbossSupperTask task=IbossSpringUtils.getBeanByTypeAndName(taskProcessClass, IbossSupperTask.class, applicationContext);
        	if(task==null) {
        		throw new Exception("未获取到有效的对象，请检查类名是否正确！类名："+taskProcessClass);
        	}
        	task.setTdMTaskCfg(tdMTaskCfg);
        	threadPoolTaskScheduler.schedule(task, new CronTrigger(taskCronExp));
        }
		
        log.info("------------------启动定时任务  完成----------------------");
		
	}
	
	/**
	 * @Description: 重启定时任务
	 * @author lenovo
	 * @throws Exception 
	 * @date 2019年3月28日 下午3:58:18
	 */
	public void restartAllTask() throws Exception {
		log.info("-------------------重启定时任务  开始----------------------");
		taskScheduledManager.loadTaskConfig();
        startAllTask();
        log.info("-------------------重启定时任务  完成----------------------");

	}
	
	/**
	 * @Description: 停止所有的定时任务
	 * @author lenovo
	 * @date 2019年3月28日 下午4:01:56
	 */
	@PreDestroy
	public void stopAllTask() {
		//不会为空
		log.info("----------------关闭定时任务线程  开始-----------------------------------");
		threadPoolTaskScheduler.shutdown();
		log.info("----------------关闭定时任务线程  完成-----------------------------------");

	}


	@Override
	public void stopAll() throws Exception {
		stopAllTask();
	}


	@Override
	public void startAll() throws Exception {
		startAllTask();
	}
	
}
