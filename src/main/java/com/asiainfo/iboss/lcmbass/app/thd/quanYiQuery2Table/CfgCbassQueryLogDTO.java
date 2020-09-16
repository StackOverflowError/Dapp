package com.asiainfo.iboss.lcmbass.app.thd.quanYiQuery2Table;

import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassQueryLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author :lenovo
 * @date :2019年7月8日 上午9:48:21
 */
@Data
public class CfgCbassQueryLogDTO implements Serializable{

	private static final long serialVersionUID = -607486245138914375L;
	private List<CfgCbassQueryLog> cfgCbassQueryLogList;

}
