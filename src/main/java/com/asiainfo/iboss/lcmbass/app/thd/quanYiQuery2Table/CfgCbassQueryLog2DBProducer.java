package com.asiainfo.iboss.lcmbass.app.thd.quanYiQuery2Table;

import com.asiainfo.iboss.lcmbass.app.thd.supper.IbossThdSupperProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @Description:从redis获取ibtrade
 * @author :asys
 * @date :2019年6月11日 下午4:55:38
 */
@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class CfgCbassQueryLog2DBProducer extends IbossThdSupperProducer {
	
	@Autowired
	private CfgCbassQueryLog2DBThdCtrl cfgCbassLog2DBThdCtrl;

	@Override
	public void run() {

	}
}
