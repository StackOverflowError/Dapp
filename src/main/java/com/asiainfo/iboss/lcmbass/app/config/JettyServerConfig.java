/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.config;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年7月23日 下午2:57:02
 */
@Configuration
public class JettyServerConfig {
	
	@Bean
	public JettyServletWebServerFactory jettyServletWebServerFactory() {
		JettyServletWebServerFactory jettyServletWebServerFactory=new JettyServletWebServerFactory();
		jettyServletWebServerFactory.addServerCustomizers(jettyServerCustomizer());
		return jettyServletWebServerFactory;
	}
	
	@Bean
	public JettyServerCustomizer jettyServerCustomizer() {
		JettyServerCustomizer jettyServerCustomizer=new JettyServerCustomizer() {
			@Override
			public void customize(Server server) {
				QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
				String maxThreadsStr=System.getProperty("jetty.threadPool.maxThreads");
				if(StringUtils.isBlank(maxThreadsStr)) {
					maxThreadsStr="200";
				}
				threadPool.setMaxThreads(Integer.valueOf(maxThreadsStr));	
			}
		};
		return jettyServerCustomizer;
	}

}
