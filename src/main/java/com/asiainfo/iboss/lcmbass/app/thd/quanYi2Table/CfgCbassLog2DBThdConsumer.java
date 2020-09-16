package com.asiainfo.iboss.lcmbass.app.thd.quanYi2Table;

import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassLogService;
import com.asiainfo.iboss.lcmbass.app.thd.supper.IbossThdSupperConsumer;
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
public class CfgCbassLog2DBThdConsumer extends IbossThdSupperConsumer<CfgCbassLogDTO> {

	@Autowired
	private CfgCbassLogService cfgCbassLogService;
	
	public void onEvent(CfgCbassLogDTO cfgCbassLogDTO){
		cfgCbassLogService.save(cfgCbassLogDTO.getCfgCbassLog());	
	}	
}
