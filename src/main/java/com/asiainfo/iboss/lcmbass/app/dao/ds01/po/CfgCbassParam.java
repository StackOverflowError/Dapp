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
 * 参数配置表
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CFG_CBASS_PARAM")
public class CfgCbassParam extends Model<CfgCbassParam> {

    private static final long serialVersionUID = 1L;

    /**
     * 系统代码
     */
    @TableId("SYSCODE")
    private String syscode;

    /**
     * 配置名称
     */
    @TableField("CONFIG_NAME")
    private String configName;

    /**
     * 配置描述
     */
    @TableField("CONFIG_DESC")
    private String configDesc;

    /**
     * 参数值序列
     */
    @TableField("VALUE_SEQ")
    private Integer valueSeq;

    /**
     * 参数名称
     */
    @TableField("PARAM_NAME")
    private String paramName;

    /**
     * 参数值
     */
    @TableField("PARAM_VALUE")
    private String paramValue;

    /**
     * 参数值描述
     */
    @TableField("VALUE_DESC")
    private String valueDesc;

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
        return this.syscode;
    }

}
