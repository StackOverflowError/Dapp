package com.asiainfo.iboss.lcmbass.app.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asiainfo.iboss.lcmbass.app.component.AppHealthManager;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年4月8日 上午9:44:44
 */
@Slf4j
@RestController
public class StateCheckHandler {
	
	@Autowired
	private AppHealthManager appHealthManager;
	
	/**
	 * 四层：http://ip:端口/services/check          --返回现有状态   true:接收请求，false：不接收请求
	 * 停止接收请求：http://ip:端口/services/check?status=die   --执行成功返回true,否则返回错误信息
	 * 开始接收请求：http://ip:端口/services/check?status=live
	 *      可停机：http://ip:端口/services/check?status=closeAble
	 * **/
	@RequestMapping("/services/check")
	public String check(@RequestParam(value="status",defaultValue="")String status){
		
		log.debug("--------------"+appHealthManager);
		try {
			log.debug("接收到的指令status为："+status);
			if(StringUtils.isNotBlank(status)) {
				if(StringUtils.equalsIgnoreCase("die", status)) {
					log.info("接收到app服务停止接收请求指令：status="+status);
					appHealthManager.setHealthStates("false");
					return "true";
				}
				if(StringUtils.equalsIgnoreCase("live", status)) {
					log.info("接收到app服务开始接收请求指令：status="+status);
					appHealthManager.setHealthStates("true");
					return "true";
				}
				if(StringUtils.equalsIgnoreCase("closeAble", status)) {
					log.info("接收到app服务可关闭状态指令：status="+status);
					appHealthManager.setHealthStates("false");
					return appHealthManager.getAppCloseEnable();
				}
				log.error("发送的指令不在可识别范围，请输入正确的指令！");
				return "false";
			}	
			return appHealthManager.getHealthStates();
		} catch (Exception e) {
			log.error("调用状态出错：{}",e);
			return "false";
		}
		
	}

}
