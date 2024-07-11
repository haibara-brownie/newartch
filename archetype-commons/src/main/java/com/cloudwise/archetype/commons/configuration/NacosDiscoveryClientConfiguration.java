package com.cloudwise.archetype.commons.configuration;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.system.SystemUtil;
import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

/**
 * nacos客户端注册至服务端时，更改服务详情中的元数据
 */
@Slf4j
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class})
public class NacosDiscoveryClientConfiguration {
    
    @Value("${server.port:0}")
    private int port;

    @Value("${management.server.port:}")
    public String metricsPort;

    @Value("${management.server.hostname:}")
    public String metricsHost;
    
    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public Map<String, String> prometheusTargetInfo() {
        Map<String, String> prometheusTargetInfo = Maps.newHashMapWithExpectedSize(9);
        prometheusTargetInfo.put("service", SpringUtil.getApplicationName());
        prometheusTargetInfo.put("port", metricsPort);
        prometheusTargetInfo.put("address", metricsHost);
        prometheusTargetInfo.put("metrics_path", "/metrics");
        prometheusTargetInfo.put("scheme", "http");
        prometheusTargetInfo.put("env", StringUtils.EMPTY);
        prometheusTargetInfo.put("region", StringUtils.EMPTY);
        prometheusTargetInfo.put("product", "archetype");
        prometheusTargetInfo.put("service_type", "cw");
        return prometheusTargetInfo;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosServiceManager nacosServiceManager,
                                 Map<String, String> prometheusTargetInfo,
                                 NacosDiscoveryProperties nacosProperties) throws JsonProcessingException {
        //更改服务详情中的元数据，增加服务注册时间
        nacosProperties.getMetadata().put("startup.time", DateUtil.now());
        nacosProperties.getMetadata().put("prometheus.target.info", objectMapper.writeValueAsString(prometheusTargetInfo));
        nacosProperties.getMetadata().put("serverPort", String.valueOf(port));
        //userInfo信息
        nacosProperties.getMetadata().put("user.info", objectMapper.writeValueAsString(SystemUtil.getUserInfo()));
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        String version;
        if (path.endsWith("jar")) {
            String[] pathStr = path.split("/");
            version = pathStr[pathStr.length - 1];
        } else {
            version = SpringUtil.getApplicationName() + "-localhost";
        }
        nacosProperties.getMetadata().put("version", version);
        //添加钩子函数
        this.nacosShutdownHook(nacosProperties, nacosServiceManager);
        return new NacosWatch(nacosServiceManager, nacosProperties);
    }

    private void nacosShutdownHook(NacosDiscoveryProperties nacosProperties, NacosServiceManager nacosServiceManager) {
        // register deregisterNacosInstance as shutdown hook
        RuntimeUtil.addShutdownHook(() -> {
            String serviceName = nacosProperties.getService();
            String groupName = nacosProperties.getGroup();
            String clusterName = nacosProperties.getClusterName();
            String ip = nacosProperties.getIp();
            int nacosPort = nacosProperties.getPort();
            try {
                nacosServiceManager.getNamingService().deregisterInstance(serviceName, groupName, ip, nacosPort, clusterName);
            } catch (NacosException e) {
                log.error("deregister from nacos error", e);
            }
            log.error("{} stop, deregisterNacosInstance.serviceName:{},groupName:{},ip:{},port:{},clusterName:{}", SpringUtil.getApplicationName(), serviceName, groupName, ip, nacosPort, clusterName);
        });
    }
}