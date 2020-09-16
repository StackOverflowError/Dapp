package com.asiainfo.iboss.lcmbass.app.dApp;

import client.EosApiRestClient;
import client.domain.common.transaction.PackedTransaction;
import client.domain.common.transaction.SignedPackedTransaction;
import client.domain.common.transaction.TransactionAction;
import client.domain.common.transaction.TransactionAuthorization;
import client.domain.request.chain.transaction.PushTransactionRequest;
import client.domain.response.chain.AbiJsonToBin;
import client.domain.response.chain.Block;
import client.domain.response.chain.ChainInfo;
import client.domain.response.chain.account.Account;
import client.domain.response.chain.account.Key;
import client.domain.response.chain.account.Permission;
import client.domain.response.chain.transaction.PushedTransaction;
import client.domain.response.history.transaction.Transaction;
import client.exception.EosApiException;
import client.util.TxPackUtil;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.iboss.lcmbass.app.dao.ds01.po.CfgCbassBusi;
import com.eos.crypto.ec.EosPrivateKey;
import com.eos.crypto.ec.EosPublicKey;
import com.eos.crypto.util.Base58;
import com.eos.crypto.util.CryptUtil;
import com.eos.crypto.util.MTRandom;
import com.google.gson.JsonObject;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.multiaddr.MultiAddress;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DappUpUtil {

    private static IPFS ipfs = new IPFS(new MultiAddress("/ip4/localhost/tcp/8080"));

    public static PushedTransaction pushTxProcess(String contractCode, String contractAction, Map createArgs, CfgCbassBusi cfgCbassBusiTemp, EosApiRestClient eosApiRestClient) throws ParseException {

        //获取区块链信息
        ChainInfo chainInfo = eosApiRestClient.getChainInfo();
        //获取最新不可逆区块
        Block block = eosApiRestClient.getBlock(chainInfo.getHeadBlockId());

        //创建交易action
        TransactionAction transactionAction = buildTransactionAction(contractCode, contractAction, createArgs, cfgCbassBusiTemp, eosApiRestClient);
        //根据区块信息block及transactionAction设置准备用于签名的交易包PackedTransaction
        PackedTransaction packedTransaction = buildPackedTransaction(block,transactionAction);

        //解锁钱包, 用于 交易签名
        try {
            eosApiRestClient.unlockWallet(cfgCbassBusiTemp.getParam1(), cfgCbassBusiTemp.getParam2());//钱包用户名、密码
        } catch (EosApiException ex) {
            System.err.println(ex.getMessage());
        }

        //构建带签名的交易signedPackedTransaction
        SignedPackedTransaction signedPackedTransaction = eosApiRestClient.signTransaction(packedTransaction,
                Collections.singletonList(cfgCbassBusiTemp.getPublicKey()),//钱包账户公钥
                chainInfo.getChainId());
        //                "4a2fb7b7aacce5ea952dc96fbac6ed648efc08c1e1577882f3f33c82da248d64"


        //将signedPackedTransaction打包成为hex字符串packTrx
        String packTrx = TxPackUtil.packTx(signedPackedTransaction);

        //将signedPackedTransaction中的签名，packTrx打包成为交易请求
        PushTransactionRequest pushTransactionRequest = new PushTransactionRequest(
                "none",
                null,
                signedPackedTransaction.getSignatures(),
                packTrx,
                null
        );
        //向区块链网络发送交易
        PushedTransaction pushedTransaction = eosApiRestClient.pushTransaction(pushTransactionRequest);
        return pushedTransaction;
    }

    public static TransactionAction buildTransactionAction(String contractCode, String contractAction, Map createArgs, CfgCbassBusi cfgCbassBusiTemp, EosApiRestClient eosApiRestClient) {
        //创建交易 action 授权
        TransactionAuthorization transactionAuthorization = new TransactionAuthorization();
        transactionAuthorization.setActor(cfgCbassBusiTemp.getActor());//指定要进行授权的eos账户
        transactionAuthorization.setPermission(cfgCbassBusiTemp.getPemission()); //使用指定eos账户的
        //将合约action 序列化为 16进制字符串，以便应用于 push_transaction  调用
        AbiJsonToBin abiJsonToBin = eosApiRestClient.abiJsonToBin(contractCode,contractAction, createArgs);

        //创建交易action
        TransactionAction transactionAction = new TransactionAction();
        transactionAction.setAccount(contractCode);// 指定合约账户
        transactionAction.setName(contractAction);// 指定要调用的合约action
        transactionAction.setData(abiJsonToBin.getBinargs());//  设置合约参数
        transactionAction.setAuthorization(Collections.singletonList(transactionAuthorization));//   设置 action 授权
        return transactionAction;
    }

    /*
     * @Author xurc
     * @Description 创建交易
     * @Date 10:37 2020/8/21
     * @Param [block, transactionAction]
     * @return client.domain.common.transaction.PackedTransaction
     **/
    public static PackedTransaction buildPackedTransaction(Block block, TransactionAction transactionAction) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date blockTimeStamp = format.parse(block.getTimeStamp());
        long expTime = blockTimeStamp.getTime()/1000 + 1200;
        Date expDate = new Date(expTime*1000);
        String expTimeStamp = format.format(expDate);


        PackedTransaction packedTransaction = new PackedTransaction();
        // 设置交易过期时间 为 20 分钟。
        packedTransaction.setExpiration(expTimeStamp);
        //设置引用最后不可逆区块的 ref_block_prefix
        packedTransaction.setRefBlockPrefix(block.getRefBlockPrefix());
        //设置引用最后不可逆区块的 ref_block_num
        packedTransaction.setRefBlockNum(block.getBlockNum());
        packedTransaction.setRegion("0");
        packedTransaction.setMax_net_usage_words(0);
        packedTransaction.setTransactionExtensions(new ArrayList<>());
        packedTransaction.setContextFreeData(new ArrayList<>());
        packedTransaction.setContextFreeActions(new ArrayList<>());
        packedTransaction.setActions(Collections.singletonList(transactionAction));

        return packedTransaction;
    }


    public static boolean TransactionConfirm(String TxId, long blockId, EosApiRestClient eosApiRestClient){
        try {
            Thread.sleep(1000*3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Transaction transaction = eosApiRestClient.getTransaction(TxId);
        if (transaction.getLastIrreversibleBlock() > blockId){
            return true;
        } else {
            return false;
        }
    }

    /**
     *  将数据进行 ECC+AES 加密后，传输到ipfs中
     * @throws UnsupportedEncodingException
     */
    public static Map uploadIpfs(JSONObject reqJson, CfgCbassBusi cfgCbassBusiTemp, EosApiRestClient eosApiRestClient) throws IOException {


        //发起方私钥，不需要
        String senderPrivateKey =  "";
        EosPrivateKey  senderECPrivateKey= new EosPrivateKey(senderPrivateKey);

        //cmbaas平台加解密使用的是对方账号的default权限的公钥
        Account accountInfo = eosApiRestClient.getAccount(cfgCbassBusiTemp.getParam5());
        List<Permission> permissions = accountInfo.getPermissions();
        String eCPublicKey = "";
        for(Permission permission : permissions){
            if("default".equals(permission)){
                Key publicKey = permission.getRequiredAuth().getKeys().get(0);
                eCPublicKey = publicKey.getKey();
                break;
            }
        }
        EosPublicKey receiverECPublicKey = new EosPublicKey(eCPublicKey);

        /**
         * 使用 发送者方私钥 和接收方公钥，生成 aes key, 对数据进行加密
         * nonce  为初始化向量，可以使用固定值，
         *                      也可以使用随机值，并使用私有协议。根据业务需求选择。
         *  请参考技术规范中约定的格式
         */
        byte[] nonce = new byte[16];
        MTRandom random=new MTRandom();
        random.nextBytes(nonce);

        // 待加密 数据
        byte[] params = reqJson.getBytes("utf8");

        log.info("原始加密数据： " + new String(params,StandardCharsets.UTF_8));

        byte[] encrypted = new byte[0];
        try {
            encrypted = CryptUtil.encrypt(senderECPrivateKey,receiverECPublicKey,nonce,params);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("  do something!!!!");
        }

        log.info("加密后数据： " + Base58.encode(encrypted));

        JsonObject object = new JsonObject();
        object.addProperty("Data", Base58.encode(encrypted));

        List<byte[]> objectData = new ArrayList<>(1);

        objectData.add(object.toString().getBytes(StandardCharsets.UTF_8));

        List<MerkleNode> merkleNodes =  ipfs.object.put(objectData);

        MerkleNode node = merkleNodes.get(0);

        /**
         * 可以将该值，上传到区块链网络中。
         */
        String nodeHash = node.hash.toBase58();

        log.info( " 上传 ipfs 后，获取到的 hash值 "+ nodeHash);

        return dealUpMap(nodeHash, cfgCbassBusiTemp);
    }

    private static Map dealUpMap(String nodeHash, CfgCbassBusi cfgCbassBusiTemp) {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("account", cfgCbassBusiTemp.getActor());//上传方区块链账号
        reqMap.put("HashValue", nodeHash);//上传IPFS后获取到的数据hash值
        reqMap.put("IsMissData", "0");//补传数据标识
        reqMap.put("HomeParty", new JSONObject().put("HomePartyID", cfgCbassBusiTemp.getParam5()));//数据下载方的区块链账号

        return reqMap;
    }

    /*
     * @Author xurc
     * @Description    对不发ipfs数据处理
     * @Date 15:18 2020/9/16
     * @Param [reqJson, cfgCbassBusiTemp, eosApiRestClient]
     * @return java.util.Map
     **/
    public static Map uploadWithoutIpfs(JSONObject reqJson, CfgCbassBusi cfgCbassBusiTemp, EosApiRestClient eosApiRestClient) {
        //暂不处理
        //
        //
        return new HashMap();
    }
}
