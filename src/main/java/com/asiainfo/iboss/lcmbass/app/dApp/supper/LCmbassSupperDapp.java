package com.asiainfo.iboss.lcmbass.app.dApp.supper;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;

public class LCmbassSupperDapp {

	public JSONObject deal(CfgCbassBusi cbassBusiCode,JSONObject reqJson,Object ... args) {
		JSONObject resJson=new JSONObject();
		resJson.put("resCode", "0000");
		resJson.put("resMsg", "处理成功！！！");
		return resJson;
	}

}
