package com.asiainfo.iboss.lcmbass.app.handler.apply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.component.CfgCbassParamManager;


/**
 * <p>Title: TdMIfConfigHandler.java</p>
 * <p>Description: </p>
 * <p>Company: 亚信科技</p> 
 * @author	asys
 * @date	2018年8月20日
 * @version 
 */
@RestController
@RequestMapping(value="config" )
public class TdMIfConfigHandler {
	
	@Autowired
	private CfgCbassParamManager tdMIfConfigManager;
	
	@RequestMapping(value="/reloadConfig",produces= {"application/json;charset=utf-8"}) 
	public String reloadConfig() {
		JSONObject json=new JSONObject();
		try {
			tdMIfConfigManager.loadConfig();
			json.put("isSuccess", "true");
			json.put("message", "reload reloadConfig table success");
		} catch (Exception e) {
			json.put("isSuccess", "false");
			json.put("message", "reload reloadConfig table fail--->"+e);
		}
		return json.toString();
	}

}
