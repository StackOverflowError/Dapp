package com.asiainfo.iboss.lcmbass.app.dao.ds01.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 业务接口配置表
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CFG_CBASS_BUSI")
public class CfgCbassBusi extends Model<CfgCbassBusi> {

    private static final long serialVersionUID = 1L;

    /**
     * 业务编码：唯一标识业务,Dapp与外围系统协商确定，如果集团规范业务，以“actionname”+“action version”为准
     */
    @TableId("COMMOND_CODE")
    private String commondCode;

    /**
     * 合约账户：集团规范或者省侧约定的合约账户信息。
     */
    @TableField("CONTRACT_ACCOUNT")
    private String contractAccount;

    /**
     * 发起方传入的业务唯一流水，用户Dapp确定关联交易
     */
    @TableField("FLOW_ID")
    private String flowId;

    /**
     * 系统接入编码，用以系统登记使用，便于识别系统
     */
    @TableField("ACCESS_SYSCODE")
    private String accessSyscode;

    /**
     * 省侧账户
     */
    @TableField("ACTOR")
    private String actor;

    /**
     * 权限
     */
    @TableField("PEMISSION")
    private String pemission;

    /**
     * 公钥   （签名）
     */
    @TableField("PUBLIC_KEY")
    private String publicKey;

    /**
     * 钱包地址
     */
    @TableField("WALLET_BASE_URL")
    private String walletBaseUrl;

    /**
     * 链地址
     */
    @TableField("CHAIN_BASE_URL")
    private String chainBaseUrl;

    /**
     * 历史地址
     */
    @TableField("HISTORY_BASE_URL")
    private String historyBaseUrl;

    /**
     * 合约账户编码
     */
    @TableField("CONTRACT_CODE")
    private String contractCode;

    /**
     * 业务动作：（取值参看集团规范）
     */
    @TableField("ACTION")
    private String action;

    /**
     * 平台编码：咪咕:MIGU（详情参看规范）
     */
    @TableField("PLAT_CODE")
    private String platCode;

    /**
     * 处理类全路径
     */
    @TableField("PROCESS_CLASS_FULL_NAME")
    private String processClassFullName;

    /**
     * 参数1 钱包用户名
     */
    @TableField("PARAM1")
    private String param1;

    /**
     * 参数1描述
     */
    @TableField("PARAM1_DESC")
    private String param1Desc;

    /**
     * 参数2  钱包密码
     */
    @TableField("PARAM2")
    private String param2;

    /**
     * 参数2描述
     */
    @TableField("PARAM2_DESC")
    private String param2Desc;

    /**
     * 参数3 （落地方账号）
     */
    @TableField("PARAM3")
    private String param3;

    /**
     * 参数3描述
     */
    @TableField("PARAM3_DESC")
    private String param3Desc;

    /**
     * 参数4 （是否上传ipfs  1--上传  0--不上传）
     */
    @TableField("PARAM4")
    private String param4;

    /**
     * 参数4描述
     */
    @TableField("PARAM4_DESC")
    private String param4Desc;

    /**
     * 参数5      数据获取方账号
     */
    @TableField("PARAM5")
    private String param5;

    /**
     * 参数5描述
     */
    @TableField("PARAM5_DESC")
    private String param5Desc;

    /**
     * 移除标识
0：移除
1：在用
     */
    @TableField("REMOVE_TAG")
    private String removeTag;

    /**
     * 操作人姓名
     */
    @TableField("OPER_NAME")
    private String operName;

    /**
     * 插入时间
     */
    @TableField("INSERT_TIME")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.commondCode;
    }

}
