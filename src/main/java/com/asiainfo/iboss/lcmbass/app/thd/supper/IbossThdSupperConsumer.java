/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.thd.supper;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassThd;
import com.lmax.disruptor.WorkHandler;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年5月7日 下午6:39:56
 */
public class IbossThdSupperConsumer<T> implements WorkHandler<T> {
	@Getter
	@Setter
	protected CfgCbassThd tdMThdCfg;

	@Override
	public void onEvent(T event) throws Exception {
		
	}
}
