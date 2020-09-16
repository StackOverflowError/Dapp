package com.asiainfo.iboss.lcmbass.app.thd.quanYiQuery2Table;

import com.asiainfo.iboss.lcmbass.app.thd.supper.IbossThdSupperExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 
 * @Description:
 * @author :asys
 * @date :2019年6月11日 下午4:50:52
 */
@Component
@Slf4j
public class CfgCbassQueryLog2DBExceptionHandler extends IbossThdSupperExceptionHandler<CfgCbassQueryLogDTO> {

	
	@Override
	public void handleEventException(Throwable ex, long sequence, CfgCbassQueryLogDTO event) {
		
		log.error("[handleEventException]线程执行时异常--->"+ex);
		
	}

	
	@Override
	public void handleOnStartException(Throwable ex) {
		
		log.error("[handleOnStartException]线程启动时异常--->"+ex);
		
	}

	
	@Override
	public void handleOnShutdownException(Throwable ex) {
		
		log.error("[handleOnShutdownException]线程关闭时异常--->"+ex);
	}

	

}
