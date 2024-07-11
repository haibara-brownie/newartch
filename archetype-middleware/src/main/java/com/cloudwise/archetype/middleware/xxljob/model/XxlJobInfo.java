package com.cloudwise.archetype.middleware.xxljob.model;

import com.cloudwise.archetype.middleware.xxljob.enums.ExecutorRouteStrategyEnum;
import com.cloudwise.archetype.middleware.xxljob.enums.MisfireStrategyEnum;
import com.cloudwise.archetype.middleware.xxljob.enums.ScheduleTypeEnum;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;


/**
 * xxl-job info
 *
 * @author xuxueli  2016-1-12 18:25:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class XxlJobInfo {

    private Integer id;                // 主键ID
    private int jobGroup;        // 执行器主键ID
    private String jobDesc;

    private Date addTime;
    private Date updateTime;

    private String author;        // 负责人
    private String alarmEmail;    // 报警邮件

    @Builder.Default
    private ScheduleTypeEnum scheduleType = ScheduleTypeEnum.CRON;            // 调度类型

    private String scheduleConf;            // 调度配置，值含义取决于调度类型
    @Builder.Default
    private MisfireStrategyEnum misfireStrategy = MisfireStrategyEnum.DO_NOTHING;            // 调度过期策略
    @Builder.Default
    private ExecutorRouteStrategyEnum executorRouteStrategy = ExecutorRouteStrategyEnum.ROUND;    // 执行器路由策略

    private String executorHandler;            // 执行器，任务Handler名称
    private String executorParam;            // 执行器，任务参数
    @Builder.Default
    private ExecutorBlockStrategyEnum executorBlockStrategy = ExecutorBlockStrategyEnum.SERIAL_EXECUTION;    // 阻塞处理策略
    private int executorTimeout;            // 任务执行超时时间，单位秒
    private int executorFailRetryCount;        // 失败重试次数
    @Builder.Default
    private GlueTypeEnum glueType = GlueTypeEnum.BEAN;
    private String glueSource;        // GLUE源代码
    private String glueRemark;        // GLUE备注
    private Date glueUpdatetime;    // GLUE更新时间

    private String childJobId;        // 子任务ID，多个逗号分隔

    private int triggerStatus;        // 调度状态：0-停止，1-运行
    private long triggerLastTime;    // 上次调度时间
    private long triggerNextTime;    // 下次调度时间

}
