/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.asiainfo.iboss.lcmbass.app.component.AppHealthManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: Boot启动完成后执行run方法，用于修改应用状态，用于接收请求！
 * @author :lenovo
 * @date :2019年5月30日 下午2:49:01
 */
@Component
@Slf4j
public class AfterBootStartedConfig implements CommandLineRunner{
	
	@Autowired
	private AppHealthManager appHealthManager;

	@Override
	public void run(String... args) throws Exception {
		log.info("-----------------iboss 应用，开始接收请求！---------------------");
		appHealthManager.setHealthStates("true");
	}
	
	
}
