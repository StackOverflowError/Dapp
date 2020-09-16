package com.asiainfo.iboss.lcmbass.app.dApp.miguDApp;

import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.dApp.miguDApp.service.impl.InvokeByMiguDAppSVImpl;
import com.asiainfo.iboss.lcmbass.app.dApp.miguDApp.service.interfaces.IInvokeByMiguDAppSV;

public class MiguDappDemo {
    public static void main(String[] args) {
        IInvokeByMiguDAppSV invokeByMiguDAppTest = new InvokeByMiguDAppSVImpl();

//        List<MiguDAppUserDataDO> userDataList = new ArrayList<>();
//        MiguDAppUserDataDO userData = new MiguDAppUserDataDO();
//        userData.setOprNumb("");
//        userData.setiDVA("");
//        userData.setiDVB("");
//        userData.setOpr("");
//        userData.setBizType("");
//        userData.setBillFlg("");
//        userData.setsPID("");
//        userData.setsPBizCode("");
//        userData.setSource("");
//        userData.setEfftT("");
//        userData.setChannel("");
//        userData.setMemberLevel("");
//        userData.setChannelCode("");
//        userData.setDeviceSN("");
//        userData.setcAMPAIGN_ID("");
//
//        MiguDAppUserExtendDataDO userExtendData = new MiguDAppUserExtendDataDO();
//        userExtendData.setOldIDV("");
//        userExtendData.setActionReasonID("");
//        userExtendData.setPseq("");
//        userExtendData.setZipCode("");
//        userExtendData.setCityCode("");
//        userExtendData.setDistrictCode("");
//        userExtendData.setAddress("");
//        userExtendData.setOldStbID("");
//        userExtendData.setOldSPID("");
//        userExtendData.setOldSPServID("");
//        userExtendData.setoTP("");
//        userExtendData.setOprTime("");
//        userExtendData.setBroadbandID("");
//
//        userData.setUserExtendData(userExtendData);
//        userDataList.add(userData);
//
//        MiguContentDataDO content = new MiguContentDataDO();
//
//        content.setCrtT("");
//        content.setPkgSeq("");
//        content.setuDSum("");
//        content.setUserData(userDataList);
//
//        MiguDAppInputDO req = new MiguDAppInputDO();
//        req.setCommondCode("");//Dapp与外围系统协商确定，如果集团规范业务，以“actionname”+“action version”为准
//        req.setContractAccount("");//合约账户：集团规范或者省侧约定的合约账户信息。
//        req.setFlowId("");//1. 发起方传入的业务唯一流水，用户Dapp确定关联交易
//        req.setAccessSyscode("");//系统接入编码，用以系统登记使用，便于识别系统
//        req.setActor("");//省侧账户
//        req.setPermission("");//权限
//        req.setPublicKey("");//公钥
//        req.setWalletBaseUrl("");//钱包地址
//        req.setChainBaseUrl("");//链地址
//        req.setHistoryBaseUrl("");
//        req.setContractCode("");//合约账户
//        req.setContractAction("");//action名称
//        req.setContent(content);
//
//        req.setTransido("");
//        req.setUserPartyID("");
//        req.setHomePartyId("");
//        req.setSignType("");
//        req.setReceiverKey("");
//        req.setCutOffDay("");

        JSONObject reqJson = new JSONObject();
        //拼装上链参数

        JSONObject miguDAppOutput = invokeByMiguDAppTest.loadUDRSyn(reqJson);


    }
}
