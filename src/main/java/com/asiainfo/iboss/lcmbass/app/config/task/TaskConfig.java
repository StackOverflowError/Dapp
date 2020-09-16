package com.asiainfo.iboss.lcmbass.app.config.task;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class TaskConfig {
	
	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler exe = new ThreadPoolTaskScheduler();
		exe.setPoolSize(30);
		exe.setThreadNamePrefix("taskExecutor-");
		exe.setWaitForTasksToCompleteOnShutdown(false);
		exe.setAwaitTerminationSeconds(60);
		exe.setRemoveOnCancelPolicy(true);
		return exe;
	}

}
