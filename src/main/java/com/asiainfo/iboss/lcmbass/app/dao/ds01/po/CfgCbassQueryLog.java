package com.asiainfo.iboss.lcmbass.app.dao.ds01.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author asys
 * @since 2020-09-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CFG_CBASS_QUERY_LOG")
public class CfgCbassQueryLog extends Model<CfgCbassQueryLog> {

    private static final long serialVersionUID = 1L;

    @TableId("LOG_ID")
    private Double logId;

    @TableField("COMMOND_CODE")
    private String commondCode;

    @TableField("ORDER_ID")
    private String orderId;

    @TableField("REMARK")
    private String remark;

    @TableField("REMARK1")
    private String remark1;

    @TableField("INSERT_TIME")
    private Date insertTime;

    @TableField("SUBORDER_ID")
    private String suborderId;

    @TableField("HASH_SUBORDER_ID")
    private String hashSuborderId;

    @TableField("PROVINCE_ID")
    private String provinceId;

    @TableField("ORDER_STATUS")
    private String orderStatus;

    @TableField("STATUS_DESC")
    private String statusDesc;

    @TableField("PROVINCE_RELATIONID")
    private String provinceRelationid;

    @TableField("CREATE_TIME")
    private Date createTime;

    @TableField("MODIFY_TIME")
    private Date modifyTime;

    @TableField("CONTEXT")
    private String context;

    @TableField("REMARK3")
    private String remark3;

    @TableField("REMARK2")
    private String remark2;


    @Override
    protected Serializable pkVal() {
        return this.logId;
    }

}
