package com.asiainfo.iboss.lcmbass.app.dApp.miguDApp.service.impl;

import client.EosApiClientFactory;
import client.EosApiRestClient;
import client.domain.response.chain.transaction.PushedTransaction;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.component.CfgCbassBusiManager;
import com.asiainfo.iboss.lcmbass.app.dApp.DappUpUtil;
import com.asiainfo.iboss.lcmbass.app.dApp.miguDApp.service.interfaces.IInvokeByMiguDAppSV;
import com.asiainfo.iboss.lcmbass.app.dApp.supper.LCmbassSupperDapp;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author xurc
 * @version V1.0
 * @Description:
 * @date 2020/8/13 14:25
 * @classname InvokeByMiguDApp.java
 */

public class InvokeByMiguDAppSVImpl extends LCmbassSupperDapp implements IInvokeByMiguDAppSV {

    @Autowired
    private CfgCbassBusiManager cfgCbassBusiManager;

    public JSONObject deal(CfgCbassBusi cbassBusi, JSONObject reqJson, Object ... args) {

        JSONObject response = new JSONObject();
        try {
            response = loadUDRSyn(reqJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject loadUDRSyn(JSONObject reqJson) {
        JSONObject response = new JSONObject();
        JSONObject result = new JSONObject();
        Map reqMap = new HashMap();
        //获取配置数据
        CfgCbassBusi cfgCbassBusiTemp = cfgCbassBusiManager.getByKindId("miGuDapp");

        EosApiRestClient eosApiRestClient = EosApiClientFactory.newInstance(
                cfgCbassBusiTemp.getWalletBaseUrl(),
                cfgCbassBusiTemp.getChainBaseUrl(),
                cfgCbassBusiTemp.getHistoryBaseUrl()
        ).newRestClient();

        String contractCode = cfgCbassBusiTemp.getContractCode();//合约账户
        String contractAction = cfgCbassBusiTemp.getAction();//action名称

//        Map migucorderMap = dealreqMap(req, response);
        //判断是否上传ipfs

        PushedTransaction pushedTransaction = null;
        try {
            if("1".equals(cfgCbassBusiTemp.getParam4())){
                reqMap = DappUpUtil.uploadIpfs(reqJson, cfgCbassBusiTemp, eosApiRestClient);
            }else {
                reqMap = DappUpUtil.uploadWithoutIpfs(reqJson, cfgCbassBusiTemp, eosApiRestClient);
            }

            pushedTransaction = DappUpUtil.pushTxProcess(contractCode, contractAction, reqMap, cfgCbassBusiTemp, eosApiRestClient);
            boolean isSuccess = DappUpUtil.TransactionConfirm(pushedTransaction.getTransactionId(),pushedTransaction.getProcessed().getBlockNum(), eosApiRestClient);
            if(isSuccess){
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

        //原生产报文，{"cutOffDay":"20190809","response":{"rspCode":"00000","rspDesc":"success.."},"transIDO":"2019080914360187989708"}
        result.put("cutOffDay", reqJson.getString("cutOffDay"));
        result.put("transIDO", reqJson.getString("transIDO"));
        result.put("response", response);

        return result;
    }

//    /*
//     * @Author xurc
//     * @Description 拼装请求报文
//     * @Date 16:08 2020/8/24
//     * @Param [resultStr]
//     * @return java.util.Map
//     **/
//    private Map dealreqMap(MiguDAppInputDO req, MiguDAppOutputDO rsp) throws Exception {
//        //生成发送方A 密钥对
//        KeyPair keyPairA = EccUtil.getKeyPair();
//
//        String pubKeyStrA = EccUtil.getPublicKeyBase64Str(keyPairA);
//        String privKeyStrA = EccUtil.getPrivateKeyBase64Str(keyPairA);
//        String pubKeyStrB = "";//公钥需要落地方确认
//
//        Map<String, Object> migucorderMap = new HashMap();
//        JSONObject migucorderJson = new JSONObject();
//
//        migucorderJson.put("Transido", "2901" + req.getTransido());//系统编码 + transIDO
//        migucorderJson.put("UserPartyID", "0000000BOSS2901");//机构DOMAIN”+”系统编码”
//        JSONArray homePartyArray = new JSONArray();
//        JSONObject homePartyJson = new JSONObject();
//        homePartyJson.put("HomePartyID", "0000000BOSS2901");
//        homePartyArray.add(homePartyJson);
//        migucorderJson.put("HomeParty", homePartyArray);//机构DOMAIN”+”系统编码”
//        migucorderJson.put("SignType","0");//0:单签 1:多签 默认为单签
//        migucorderJson.put("ReceiverKey", pubKeyStrB);
//        rsp.setDappFlowId(req.getFlowId());
//
//        String reqStr = JSONObject.toJSONString(req.getContent());//处理content内容
//        //加密，发起方使用自己的私钥和落地方的公钥，私钥额外生成，公钥需要落地方确认
//        EncryptEnvelopeRequest encryptEnvelopeRequest = new EncryptEnvelopeRequest(reqStr,pubKeyStrB, privKeyStrA);
//        EncryptEnvelopeResponse encryptEnvelopeResponse = EnvelopeUtil.encryptEnvelope(encryptEnvelopeRequest);
//        migucorderJson.put("Content", encryptEnvelopeResponse.getEncryptedData());
//
//
//        Iterator it =migucorderJson.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
//            migucorderMap.put(entry.getKey(), entry.getValue());
//        }
//
//        return migucorderMap;
//    }

//
//    private PushedTransaction pushTxProcess(String contractCode, String contractAction, Map createArgs, MiguDAppInputDO req, EosApiRestClient eosApiRestClient) throws ParseException {
//
//        //获取区块链信息
//        ChainInfo chainInfo = eosApiRestClient.getChainInfo();
//        //获取最新不可逆区块
//        Block block = eosApiRestClient.getBlock(chainInfo.getHeadBlockId());
//
//        //创建交易action
//        TransactionAction transactionAction = buildTransactionAction(contractCode, contractAction, createArgs, req, eosApiRestClient);
//        //根据区块信息block及transactionAction设置准备用于签名的交易包PackedTransaction
//        PackedTransaction packedTransaction = buildPackedTransaction(block,transactionAction);
//
//        //解锁钱包, 用于 交易签名
//        try {
//            eosApiRestClient.unlockWallet("EOS7zNMvfwBYLtFcDQioMCXK47f5YLM2MeBfyCKYcQXyDYtnaUrgZ", "5KSNAqXQW9FGvAyWAiNCbEDBNDLXqtjtE8PFPdDpMsVsji1t7Xi");
//        } catch (EosApiException ex) {
//            System.err.println(ex.getMessage());
//        }
//
//        //构建带签名的交易signedPackedTransaction
//        SignedPackedTransaction signedPackedTransaction = eosApiRestClient.signTransaction(packedTransaction,
//                Collections.singletonList(req.getPublicKey()),//钱包账户公钥
//                chainInfo.getChainId());
//        //                "4a2fb7b7aacce5ea952dc96fbac6ed648efc08c1e1577882f3f33c82da248d64"
//
//
//        //将signedPackedTransaction打包成为hex字符串packTrx
//        String packTrx = TxPackUtil.packTx(signedPackedTransaction);
//
//        //将signedPackedTransaction中的签名，packTrx打包成为交易请求
//        PushTransactionRequest pushTransactionRequest = new PushTransactionRequest(
//                "none",
//                null,
//                signedPackedTransaction.getSignatures(),
//                packTrx,
//                null
//        );
//        //向区块链网络发送交易
//        PushedTransaction pushedTransaction = eosApiRestClient.pushTransaction(pushTransactionRequest);
//        return pushedTransaction;
//    }
//
//    private TransactionAction buildTransactionAction(String contractCode, String contractAction, Map createArgs, MiguDAppInputDO req, EosApiRestClient eosApiRestClient) {
//        //创建交易 action 授权
//        TransactionAuthorization transactionAuthorization = new TransactionAuthorization();
//        transactionAuthorization.setActor(req.getActor());//指定要进行授权的eos账户
//        transactionAuthorization.setPermission(req.getPermission()); //使用指定eos账户的
//        //将合约action 序列化为 16进制字符串，以便应用于 push_transaction  调用
//        AbiJsonToBin abiJsonToBin = eosApiRestClient.abiJsonToBin(contractCode,contractAction, createArgs);
//
//        //创建交易action
//        TransactionAction transactionAction = new TransactionAction();
//        transactionAction.setAccount(contractCode);// 指定合约账户
//        transactionAction.setName(contractAction);// 指定要调用的合约action
//        transactionAction.setData(abiJsonToBin.getBinargs());//  设置合约参数
//        transactionAction.setAuthorization(Collections.singletonList(transactionAuthorization));//   设置 action 授权
//        return transactionAction;
//    }
//
//    /*
//     * @Author xurc
//     * @Description 创建交易
//     * @Date 10:37 2020/8/21
//     * @Param [block, transactionAction]
//     * @return client.domain.common.transaction.PackedTransaction
//     **/
//    private PackedTransaction buildPackedTransaction(Block block, TransactionAction transactionAction) throws ParseException {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        format.setTimeZone(TimeZone.getTimeZone("GMT"));
//
//        Date blockTimeStamp = format.parse(block.getTimeStamp());
//        long expTime = blockTimeStamp.getTime()/1000 + 1200;
//        Date expDate = new Date(expTime*1000);
//        String expTimeStamp = format.format(expDate);
//
//
//        PackedTransaction packedTransaction = new PackedTransaction();
//        // 设置交易过期时间 为 20 分钟。
//        packedTransaction.setExpiration(expTimeStamp);
//        //设置引用最后不可逆区块的 ref_block_prefix
//        packedTransaction.setRefBlockPrefix(block.getRefBlockPrefix());
//        //设置引用最后不可逆区块的 ref_block_num
//        packedTransaction.setRefBlockNum(block.getBlockNum());
//        packedTransaction.setRegion("0");
//        packedTransaction.setMax_net_usage_words(0);
//        packedTransaction.setTransactionExtensions(new ArrayList<>());
//        packedTransaction.setContextFreeData(new ArrayList<>());
//        packedTransaction.setContextFreeActions(new ArrayList<>());
//        packedTransaction.setActions(Collections.singletonList(transactionAction));
//
//        return packedTransaction;
//    }
//
//    /**
//     * @Description: 将JSONObject转换为IData
//     * @author lenovo
//     * @date 2019年5月31日 下午3:28:37
//     */
//    @SuppressWarnings("unchecked")
//    static Map json2Idata(JSONObject json) {
//        if(json==null) {
//            return null;
//        }
//        Set<String> keySet = json.keySet();
//        if(keySet==null || keySet.size()==0) {
//            return null;
//        }
//
//        Map data=new HashMap();
//        for(String key:keySet) {
//            Object object = json.get(key);
//            if(object instanceof JSONObject) {
//                JSONObject subJson=(JSONObject) object;
//                Map subIdata = json2Idata(subJson);
//                if(subIdata!=null) {
//                    data.put(key, subIdata);
//                }
//
//            }else if(object instanceof JSONArray) {
//                JSONArray subArray=(JSONArray) object;
//                List subIdataSet = jsonArray2IDataSet(subArray);
//                if(subIdataSet !=null) {
//                    data.put(key, subIdataSet);
//                }
//            }else {
//                data.put(key, object);
//            }
//        }
//        return data;
//    }
//    /**
//     * @Description: 将JSONArray转换为IData
//     * @author lenovo
//     * @date 2019年5月31日 下午3:28:37
//     */
//    @SuppressWarnings("unchecked")
//    static List jsonArray2IDataSet(JSONArray array) {
//        if(array==null || array.size()==0) {
//            return null;
//        }
//        List dataSet=new ArrayList();
//        for(int i=0;i<array.size();i++) {
//            Object object = array.get(i);
//            if(object instanceof JSONObject) {
//                JSONObject subJson=(JSONObject) object;
//                Map subIdata = json2Idata(subJson);
//                if(subIdata!=null) {
//                    dataSet.add(subIdata);
//                }
//            }else if(object instanceof JSONArray) {
//                JSONArray subArray=(JSONArray) object;
//                List subDataSet = jsonArray2IDataSet(subArray);
//                if(subDataSet!=null) {
//                    dataSet.add(subDataSet);
//                }
//            }else {
//                dataSet.add(object);
//            }
//        }
//        return dataSet;
//    }
//
//    boolean TransactionConfirm(String TxId, long blockId, EosApiRestClient eosApiRestClient){
//        try {
//            Thread.sleep(1000*3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Transaction transaction = eosApiRestClient.getTransaction(TxId);
//        if (transaction.getLastIrreversibleBlock() > blockId){
//            return true;
//        } else {
//            return false;
//        }
//    }

}

