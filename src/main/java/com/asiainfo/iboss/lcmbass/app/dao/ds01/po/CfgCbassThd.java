package com.asiainfo.iboss.lcmbass.app.dao.ds01.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 线程配置表
1)多生产者-多消费者
 * </p>
 *
 * @author asys
 * @since 2020-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CFG_CBASS_THD")
public class CfgCbassThd extends Model<CfgCbassThd> {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    @TableField("THD_NAME")
    private String thdName;

    /**
     * 处理线程所在域名
中间分号分隔,所有域可用PUBLIC；
多个域用W_IBSA;W_IBSB;W_IBSC
     */
    @TableField("THD_PROCESS_DOMAIN")
    private String thdProcessDomain;

    /**
     * 任务描述
     */
    @TableField("THD_DESC")
    private String thdDesc;

    /**
     * 队列大小
必须是2^n，默认1024*1024=1048576
     */
    @TableField("BUFFER_SIZE")
    private Integer bufferSize;

    /**
     * 队列等待策略
根据策略名获取对应的等待策略，如果不是SleepingWaitStrategy、BlockingWaitStrategy 则返回 YieldingWaitStrategy
     */
    @TableField("WAIT_STRATEGY")
    private String waitStrategy;

    /**
     * 传输类全名
     */
    @TableField("THD_DTO_CLASS")
    private String thdDtoClass;

    /**
     * 任务处理类全名
     */
    @TableField("THD_CTRL_CLASS")
    private String thdCtrlClass;

    /**
     * 任务处理类全名
     */
    @TableField("THD_CONSUMER_CLASS")
    private String thdConsumerClass;

    /**
     * 消费线程池大小
默认10
     */
    @TableField("THD_CONSUMER_MAX_POOL_SIZE")
    private Integer thdConsumerMaxPoolSize;

    /**
     * 消费者数目
尽量小于或等于消费者线程池大小
默认10
     */
    @TableField("THD_CONSUMER_SIZE")
    private Integer thdConsumerSize;

    /**
     * 线程异常处理类全名
     */
    @TableField("THD_EXCEPTION_HANDLER_CLASS")
    private String thdExceptionHandlerClass;

    /**
     * 任务处理类全名
     */
    @TableField("THD_PRODUCER_CLASS")
    private String thdProducerClass;

    /**
     * 生产线程池大小
不一定需要，根据业务添加
     */
    @TableField("THD_PRODUCER_MAX_POOL_SIZE")
    private Integer thdProducerMaxPoolSize;

    /**
     * 生产者数目
尽量小于或等于生产者线程池大小
不一定需要，根据业务添加
     */
    @TableField("THD_PRODUCER_SIZE")
    private Integer thdProducerSize;

    /**
     * 生产者轮询休眠时间
不一定需要，根据业务添加
     */
    @TableField("THD_PRODUCER_SLEEP_TIME")
    private Integer thdProducerSleepTime;

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
        return null;
    }

}
