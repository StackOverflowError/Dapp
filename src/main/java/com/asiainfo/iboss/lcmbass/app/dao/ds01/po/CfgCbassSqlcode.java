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
 * SQL配置表
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CFG_CBASS_SQLCODE")
public class CfgCbassSqlcode extends Model<CfgCbassSqlcode> {

    private static final long serialVersionUID = 1L;

    /**
     * 域名
     */
    @TableId("SYSCODE")
    private String syscode;

    /**
     * 表名
     */
    @TableField("TABLE_NAME")
    private String tableName;

    /**
     * SQL标志
     */
    @TableField("SQL_TAG")
    private String sqlTag;

    /**
     * SQL语句
     */
    @TableField("SQL_STMT")
    private String sqlStmt;

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
