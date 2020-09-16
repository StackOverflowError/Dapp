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
 * 定时任务配置表
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CFG_CBASS_TASK")
public class CfgCbassTask extends Model<CfgCbassTask> {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @TableId("TASK_NAME")
    private String taskName;

    /**
     * 任务描述
     */
    @TableField("TASK_DESC")
    private String taskDesc;

    /**
     * 任务处理类全名
     */
    @TableField("TASK_PROCESS_CLASS")
    private String taskProcessClass;

    /**
     * 任务Cron表达式
     */
    @TableField("TASK_CRON_EXP")
    private String taskCronExp;

    /**
     * 处理任务所在域名
中间分号分隔,所有域可用PUBLIC；
多个域用W_IBSA;W_IBSB;W_IBSC
     */
    @TableField("TASK_PROCESS_DOMAIN")
    private String taskProcessDomain;

    /**
     * 移除标识
0：移除
1：在用
     */
    @TableField("REMOVE_TAG")
    private String removeTag;

    /**
     * 参数1
     */
    @TableField("PARAM1")
    private String param1;

    /**
     * 参数1描述
     */
    @TableField("PARAM1_DESC")
    private String param1Desc;

    /**
     * 参数1
     */
    @TableField("PARAM2")
    private String param2;

    /**
     * 参数2描述
     */
    @TableField("PARAM2_DESC")
    private String param2Desc;

    /**
     * 参数1
     */
    @TableField("PARAM3")
    private String param3;

    /**
     * 参数3描述
     */
    @TableField("PARAM3_DESC")
    private String param3Desc;

    /**
     * 参数1
     */
    @TableField("PARAM4")
    private String param4;

    /**
     * 参数4描述
     */
    @TableField("PARAM4_DESC")
    private String param4Desc;

    /**
     * 参数1
     */
    @TableField("PARAM5")
    private String param5;

    /**
     * 参数5描述
     */
    @TableField("PARAM5_DESC")
    private String param5Desc;

    @TableField("INSERT_TIME")
    private Date insertTime;

    @TableField("OPER_NAME")
    private String operName;

    @TableField("UPDATE_TIME")
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.taskName;
    }

}
