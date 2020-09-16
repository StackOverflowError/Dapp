/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.thd.supper;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassThd;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 生产者
 * @author :lenovo
 * @date :2019年5月8日 上午11:05:17
 */
public abstract class IbossThdSupperProducer implements Runnable{
	@Getter
	@Setter
	protected CfgCbassThd tdMThdCfg;
}
