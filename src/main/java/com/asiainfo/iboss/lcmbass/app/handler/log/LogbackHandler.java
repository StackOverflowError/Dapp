/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.handler.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年6月20日 下午8:48:39
 */
@RestController
@RequestMapping("/logback")
public class LogbackHandler {
	
	 /**
     * logback动态修改包名的日志级别
     * @param level 日志级别
     * @param packageName 包名
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/changeLogLevel")
    public String changeLogLevel( @RequestParam(value="level") String level,
                                  @RequestParam(value="packageName",defaultValue = "") String packageName) throws Exception {
        LoggerContext loggerContext =  (LoggerContext) LoggerFactory.getILoggerFactory();
        
        //校验level是否符合要求
        if(StringUtils.equalsIgnoreCase("DEBUG", level)) {
        	level="DEBUG";
        }else if(StringUtils.equalsIgnoreCase("INFO", level)) {
        	level="INFO";
        }else if(StringUtils.equalsIgnoreCase("WARN", level)) {
        	level="WARN";
        }else if(StringUtils.equalsIgnoreCase("ERROR", level)) {
        	level="ERROR";
        }else {
        	return "fail required Parameter level="+level+" unrecognizability;Range of parameters [ERROR、WARN、INFO、DEBUG]";
        }

        if(StringUtils.isNotBlank(packageName)) {
            loggerContext.getLogger(packageName).setLevel(Level.toLevel(level));
        	return "now the package "+packageName+" log level is "+level;
        }
        loggerContext.getLogger("root").setLevel(Level.toLevel(level));
        return "now the log level is "+level;
    }

}
