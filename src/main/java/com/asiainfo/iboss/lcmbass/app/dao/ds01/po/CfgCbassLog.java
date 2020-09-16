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
@TableName("CFG_CBASS_LOG")
public class CfgCbassLog extends Model<CfgCbassLog> {

    private static final long serialVersionUID = 1L;

    @TableId("LOG_ID")
    private Double logId;

    @TableField("COMMOND_CODE")
    private String commondCode;

    @TableField("TRADE_ID")
    private String tradeId;

    @TableField("CREATE_DATE")
    private Date createDate;

    @TableField("REQ_CONTENT")
    private String reqContent;

    @TableField("RES_CONTENT")
    private String resContent;

    @TableField("REMARK")
    private String remark;

    @TableField("REMARK1")
    private String remark1;

    @TableField("ORDER_ID")
    private String orderId;


    @Override
    protected Serializable pkVal() {
        return this.logId;
    }

}
