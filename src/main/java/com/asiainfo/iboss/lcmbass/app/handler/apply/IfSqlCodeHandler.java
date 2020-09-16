package com.asiainfo.iboss.lcmbass.app.handler.apply;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.component.CfgCbassSqlcodeManager;


/**
 * <p>
 * 重载TD_M_IFSQLCODE表Controller类
 * </p>
 * @author asys
 * @since 2019-03-19
 */
@RestController
@RequestMapping(value="sqlCode")
public class IfSqlCodeHandler {
	
	@Autowired
	private CfgCbassSqlcodeManager tdMIfsqlCodeManager;
	
	@RequestMapping(value="/reloadSql",produces= {"application/json;charset=utf-8"})
	public String reloadSql() {
		JSONObject json=new JSONObject();
		try {
			tdMIfsqlCodeManager.loadSql();
			json.put("isSuccess", "true");
			json.put("message", "reload sqlCode table success");
		} catch (Exception e) {
			json.put("isSuccess", "false");
			json.put("message", "reload sqlCode table fail--->"+e);
		}
		return json.toString();
	}
	
}
