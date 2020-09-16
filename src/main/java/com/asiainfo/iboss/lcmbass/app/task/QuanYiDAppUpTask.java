/**
 * 
 */
package com.asiainfo.iboss.lcmbass.app.task;

import client.EosApiClientFactory;
import client.EosApiRestClient;
import client.domain.response.chain.transaction.PushedTransaction;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.component.CfgCbassBusiManager;
import com.asiainfo.iboss.lcmbass.app.dApp.DappUpUtil;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassLog;
import com.asiainfo.iboss.lcmbass.app.thd.quanYi2Table.CfgCbassLog2DBThdCtrl;
import com.asiainfo.iboss.lcmbass.app.thd.quanYi2Table.CfgCbassLogDTO;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description: 权益 定时任务超类
 * @author :lenovo
 * @date :2019年4月23日 上午10:07:15
 */
@Slf4j
@Component
public class QuanYiDAppUpTask extends IbossSupperTask{

	@Autowired
	@Qualifier("NG_CRM_DS_01")
	private DataSource dataSource;


	@Autowired
	private CfgCbassBusiManager cfgCbassBusiManager;

	@Autowired
	public CfgCbassLog2DBThdCtrl cfgCbassLog2DBThdCtrl;

	@Override
	public void run() {
		JSONObject reqJson = new JSONObject();
		try {
			deal("quanYi", reqJson, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject deal(String cbassBusiCode,JSONObject reqJson,Object ... args)  throws Exception {

		//crm库获取权益数据
		PreparedStatement pstmt1 = null;
		JSONObject response = new JSONObject();

		String queryCrmSql = "select su.state,su.STATUS_DESC,su.trade_id,su.order_id,su.suborder_id\n" +
				"  from TF_B_CTRM_GERLSUBORDER su\n" +
				" where su.NUMBER_OPRTYPE = '13' and (su.state='PS' and su.status_desc='同步订单成功') or su.state='PU'" +
				"   and su.rsrv_str15 is null";

		
		//查询crm获取修改数据
		@Cleanup
		Connection conn = dataSource.getConnection();
		@Cleanup
		PreparedStatement pstmt = conn.prepareStatement(queryCrmSql);
		@Cleanup
		ResultSet rs = pstmt.executeQuery();
		JSONObject reqQueryJson = new JSONObject();
		if(rs.next()){
			reqQueryJson.put("orderStatus", rs.getString("state"));
			reqQueryJson.put("statusDesc", rs.getString("STATUS_DESC"));
			reqQueryJson.put("provinceRelationId", rs.getString("trade_id"));
			reqQueryJson.put("orderId", rs.getString("order_id"));
			reqQueryJson.put("suborderId", rs.getString("suborder_id"));
		}else {
			return (JSONObject) JSONObject.toJSON(response);
		}
		reqQueryJson.put("COMMOND_CODE", cbassBusiCode);//quanYi

		//更新状态
		String updateCrmSql = "update TF_B_CTRM_GERLSUBORDER set rsrv_str15 = '02' where order_id='" +
				rs.getString("order_id") +
				"'";
		pstmt1 = conn.prepareStatement(updateCrmSql);
		pstmt1.execute();

		//上链
		response = loadUDRSyn(reqQueryJson);
		
		return response;
	}

	public JSONObject loadUDRSyn(JSONObject reqJson) throws Exception {
		JSONObject response = new JSONObject();
		Map reqMap = new HashMap();
		//获取配置数据
		CfgCbassBusi cfgCbassBusiTemp = cfgCbassBusiManager.getByKindId("quanYiDapp");

		EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance(
				cfgCbassBusiTemp.getWalletBaseUrl(),
				cfgCbassBusiTemp.getChainBaseUrl(),
				cfgCbassBusiTemp.getHistoryBaseUrl()
		).newRestClient();
		String contractCode = cfgCbassBusiTemp.getContractCode();//合约账户
		String contractAction = cfgCbassBusiTemp.getAction();//action名称

		reqJson.put("provinceId", cfgCbassBusiTemp.getActor());//本省区块链账号
		reqJson.put("modifyTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		//将JSONObject转成map
		Iterator it =reqJson.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
			reqMap.put(entry.getKey(), entry.getValue());
		}

		PushedTransaction pushedTransaction = null;
		try {
			pushedTransaction = DappUpUtil.pushTxProcess(contractCode, contractAction, reqMap, cfgCbassBusiTemp, eosApiRestClient);
			boolean isSuccess = DappUpUtil.TransactionConfirm(pushedTransaction.getTransactionId(),pushedTransaction.getProcessed().getBlockNum(), eosApiRestClient);
			response.put("transactionId", pushedTransaction.getTransactionId());
			response.put("Id", pushedTransaction.getProcessed().getId());
			response.put("blockNum", pushedTransaction.getProcessed().getBlockNum());
			response.put("blockTime", pushedTransaction.getProcessed().getBlockTime());

			if(isSuccess){
				insert2DB(reqJson, response);
//				inDAppOrderTableSVImpl.insertTable(reqJson.toString(), response.toString());
				response.put("respCode", "0000");
				response.put("respDesc", "成功");
			}else {
				response.put("respCode", "9999");
				response.put("respDesc", "上链失败");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("respCode", "2999");
			response.put("respDesc", "上链异常");
		}


		return (JSONObject) JSONObject.toJSON(response);
	}

	private void insert2DB(JSONObject reqJson, JSONObject response) {
		JSONObject reqObject = JSON.parseObject(reqJson.toString());
		String tradeId = (String)reqObject.get("provinceRelationId");
		CfgCbassLog cfgCbassLog = new CfgCbassLog()
                .setCommondCode("quanYi")
                .setCreateDate(new Date())
                .setReqContent(reqJson.toString())
                .setResContent(response.toString())
                .setOrderId(tradeId);
		CfgCbassLogDTO cfgCbassLogDTO = new CfgCbassLogDTO();
		cfgCbassLogDTO.setCfgCbassLog(cfgCbassLog);
		cfgCbassLog2DBThdCtrl.onEvent(cfgCbassLogDTO);
	}
}
