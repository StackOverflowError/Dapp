package com.asiainfo.iboss.lcmbass.app.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.component.AppHealthManager;
import com.asiainfo.iboss.lcmbass.app.component.CfgCbassBusiManager;
import com.asiainfo.iboss.lcmbass.app.component.SequenceProducer;
import com.asiainfo.iboss.lcmbass.app.dApp.supper.LCmbassSupperDapp;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.asiainfo.iboss.lcmbass.app.utils.IbossSpringUtils;
import com.asiainfo.iboss.lcmbass.app.utils.IbossTimeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LCmbassHandler {
	
	@Autowired
	private SequenceProducer sequenceProducer;
	
	@Autowired
	private AppHealthManager appHealthManager;
	
	@Autowired
	private CfgCbassBusiManager cfgCbassBusiManager;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@RequestMapping(value="/Lcmbass")
	public void dealOrig(HttpServletRequest request, HttpServletResponse response)throws IOException {
		appHealthManager.setLastBusiTime(System.currentTimeMillis());
		request.setCharacterEncoding(StandardCharsets.UTF_8.name());
		String accepttime = IbossTimeUtils.getCurrent22Date();
		String ibsysid = sequenceProducer.getIbsysid();
		//唯一标识
		MDC.put("LOG_ID", ibsysid);
		log.info("接收到crm请求,当前时间：{}", accepttime);
		
		JSONObject resJson=new JSONObject();
		try {
			String reqJsonStr = IOUtils.toString(request.getInputStream());
			log.info("【--reqJson String--】"+reqJsonStr);
			if(StringUtils.isBlank(reqJsonStr)) {
				throw new Exception("请求数据为空，不允许操作！！！");
			}

			JSONObject reqJson=JSON.parseObject(reqJsonStr);
			String commondCode = reqJson.getString("commondCode");
			if(StringUtils.isBlank(commondCode)) {
				throw new Exception("请求数据中未传入有效的commondCode，不允许");
			}
			MDC.put("LOG_ID", ibsysid+" "+commondCode);
			CfgCbassBusi cfgCbassBusi = cfgCbassBusiManager.getByKindId(commondCode);
			if(cfgCbassBusi == null) {
				log.error("业务"+commondCode+"未配置业务接口表：CFG_CBASS_BUSI，请先配置后重试，谢谢！！！");
				throw new Exception("LCmbass平台未配置业务接口，请联系LCmbass平台管理员！！！");
			}
			String processClassFullName = cfgCbassBusi.getProcessClassFullName();
			LCmbassSupperDapp translator = IbossSpringUtils.getBeanByTypeAndName(processClassFullName, LCmbassSupperDapp.class, applicationContext);
			resJson=translator.deal(cfgCbassBusi, reqJson, null);
		} catch (Exception e) {
			resJson.put("resCode", "2999");
			resJson.put("resMsg", "系统错误！！！");
			resJson.put("errMsg", e.getMessage());
		}

		response.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
		response.setHeader("transfer-encoding", "chunked");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		
		PrintWriter pw = response.getWriter();
		pw.println(JSON.toJSONString(resJson));
		pw.flush();
		pw.close();
		String finishtime=IbossTimeUtils.getCurrent22Date();
		MDC.remove("LOG_ID");
		log.info("返回给CRM，当前时间：{}", finishtime);
	}

}
