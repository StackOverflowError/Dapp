/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.task;

import client.EosApiClientFactory;
import client.EosApiRestClient;
import client.domain.request.chain.GetTableRowsRequest;
import client.domain.response.chain.TableRow;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.component.CfgCbassBusiManager;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassQueryLog;
import com.asiainfo.iboss.lcmbass.app.service.face.CfgCbassBusiService;
import com.asiainfo.iboss.lcmbass.app.thd.quanYiQuery2Table.CfgCbassQueryLog2DBThdCtrl;
import com.asiainfo.iboss.lcmbass.app.thd.quanYiQuery2Table.CfgCbassQueryLogDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 权益 定时任务超类
 * @author :lenovo
 * @date :2019年4月23日 上午10:07:15
 */
@Slf4j
@Component
public class QuanYiQueryDAppTask extends IbossSupperTask {

	@Autowired
	private CfgCbassBusiManager cfgCbassBusiManager;

	@Autowired
	public CfgCbassQueryLog2DBThdCtrl cfgCbassQueryLog2DBThdCtrl;

	@Override
	public void run() {
		JSONObject reqJson = new JSONObject();
		queryByModifyTime();
	}

	public void queryByModifyTime(){
		GetTableRowsRequest getTableRowsRequest = new GetTableRowsRequest();

		CfgCbassBusi cfgCbassBusiTemp = cfgCbassBusiManager.getByKindId("quanYi");

		EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance(
				cfgCbassBusiTemp.getWalletBaseUrl(),
				cfgCbassBusiTemp.getChainBaseUrl(),
				cfgCbassBusiTemp.getHistoryBaseUrl()
		).newRestClient();
		String contractCode = cfgCbassBusiTemp.getContractCode();//合约账户
		String tableName = cfgCbassBusiTemp.getParam3();//table名称
		getTableRowsRequest.setCode(contractCode);
		getTableRowsRequest.setScope(contractCode);
		getTableRowsRequest.setTable(tableName);

		//索引位置，目前关于时间的索引位置为3
		getTableRowsRequest.setIndexPosition(3);
//        getTableRowsRequest.setTableKey("bymoditime");
		getTableRowsRequest.setKeyType("i64");
//        getTableRowsRequest.setUpperBound("20200706092247");
		String str = getDateFormat(30);

		getTableRowsRequest.setLowerBound(str);
		getTableRowsRequest.setLimit(20);

		TableRow getTableRows = eosApiRestClient.getTableRows(getTableRowsRequest);
		try {
			insertQueryTable(getTableRows);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//还存在数据
		while (getTableRows.getMore()){
			Map tableRowsFirst = getTableRows.getRows().get(0);
			Map tableRowsEnd = getTableRows.getRows().get(getTableRows.getRows().size()-1);
			long tableRowsEndModifyTime = Long.parseLong(tableRowsEnd.get("modifyTime").toString());
			long tableRowsFirstModifyTime = Long.parseLong(tableRowsFirst.get("modifyTime").toString());
			if (tableRowsEndModifyTime > tableRowsFirstModifyTime){
				// 缩小时间范围，继续检索
				getTableRowsRequest.setLowerBound((String) tableRowsEnd.get("modifyTime"));
			} else if(tableRowsEndModifyTime == tableRowsFirstModifyTime) {
				// 说明在同个时间点存在多个订单数据 ，扩大检索数量
				getTableRowsRequest.setLimit(40);
			}
			getTableRows = eosApiRestClient.getTableRows(getTableRowsRequest);
			try {
				insertQueryTable(getTableRows);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void insertQueryTable(TableRow getTableRows) throws Exception {

		List<CfgCbassQueryLog> cfgCbassQueryLogList = new ArrayList<>(getTableRows.getRows().size());

		for(Map<String, ?> row : getTableRows.getRows()){
			JSONObject res = mapToJson((Map<String, Object>) row);
			CfgCbassQueryLog cfgCbassQueryLog = new CfgCbassQueryLog()
					.setOrderId(res.getString("orderId"))
					.setSuborderId(res.getString("suborderId"))
					.setHashSuborderId(res.getString("hash_suborderId"))
					.setProvinceId(res.getString("ProvinceId"))
					.setOrderStatus(res.getString("orderStatus"))
					.setStatusDesc(res.getString("statusDesc"))
					.setProvinceRelationid(res.getString("provinceRelationId"))
					.setCreateTime(new SimpleDateFormat("yyyyMMddHHmmss")
							.parse(res.getString("createTime")))
					.setModifyTime(new SimpleDateFormat("yyyyMMddHHmmss")
							.parse(res.getString("modifyTime")))
					.setContext(res.getString("Context"))
					.setCommondCode("quanYi")
					.setInsertTime(new Date());
			cfgCbassQueryLogList.add(cfgCbassQueryLog);
		}
		CfgCbassQueryLogDTO cfgCbassQueryLogDTO = new CfgCbassQueryLogDTO();
		cfgCbassQueryLogDTO.setCfgCbassQueryLogList(cfgCbassQueryLogList);
		cfgCbassQueryLog2DBThdCtrl.onEvent(cfgCbassQueryLogDTO);
//		inDAppQueryTableSVImpl.insertTable(resList);

	}


	private String getDateFormat(int second) {
		//获取前30s
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar c = new GregorianCalendar();
		Date date = new Date();
		c.setTime(date);//设置参数时间
		c.add(Calendar.SECOND,-second);//把日期往后增加SECOND 秒.整数往后推,负数往前移动
		date=c.getTime(); //这个时间就是日期往后推一天的结果
		return df.format(date);
	}

	public static JSONObject mapToJson(Map<String,Object> map){
		JSONObject json = new JSONObject();
		json = new JSONObject(map);
		return json;
	}

}
