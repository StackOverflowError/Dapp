package com.asiainfo.iboss.lcmbass.app.thd.quanYiQuery2Table;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassQueryLog;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassQueryLogService;
import com.asiainfo.iboss.lcmbass.app.thd.supper.IbossThdSupperConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description:将生产者的数据插入数据库
 * @author :asys
 * @date :2019年6月11日 下午4:57:08
 */
@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class CfgCbassQueryLog2DBThdConsumer extends IbossThdSupperConsumer<CfgCbassQueryLogDTO> {
	private static transient Logger dblog=LoggerFactory.getLogger("dblog");

	@Autowired
	private CfgCbassQueryLogService cfgCbassQueryLogService;
	
	public void onEvent(CfgCbassQueryLogDTO cfgCbassQueryLogDTO){
		List<CfgCbassQueryLog> list = cfgCbassQueryLogDTO.getCfgCbassQueryLogList();
		for(CfgCbassQueryLog log:list) {
			cfgCbassQueryLogService.save(log);
		}
	}	
}
