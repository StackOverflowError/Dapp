package com.asiainfo.iboss.lcmbass.app.task;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IbossTestTask extends IbossSupperTask{
	
	@Override
	public void run() {
		log.info("我就是一个测试Task!!!");
	}

}
