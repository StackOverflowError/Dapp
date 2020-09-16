/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.component;

import java.util.UUID;
import org.springframework.stereotype.Component;
import com.asiainfo.iboss.lcmbass.app.utils.IbossTimeUtils;


/**
 * 
 * <p>Title: SequenceProducer.java</p>
 * <p>Description: </p>
 * <p>Company: 亚信科技</p> 
 * @author	asys
 * @date	2018年6月8日
 * @version 
 */
@Component("sequenceProducer")
public class SequenceProducer {
	public String getIbsysid() {
		String nowTime = IbossTimeUtils.getCurrentDate(IbossTimeUtils.S_Date_Format_20);
		String seq=String.format("%08d", UUID.randomUUID().toString().hashCode());
		return nowTime+seq;
	}
}
