package com.cloudwise.archetype.middleware.xxljob.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cloudwise.archetype.commons.response.BaseException;
import com.cloudwise.archetype.commons.response.BaseExceptionCode;
import com.cloudwise.archetype.middleware.xxljob.model.XxlJobGroup;
import com.cloudwise.archetype.middleware.xxljob.model.XxlJobInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author pio.wang
 * @Since: 2023-08-06 15:28
 */
@Service
@Slf4j
public class XxlJobService {
    @Resource
    private XxlJobConfig xxlJobConfig;

    /**
     * 注册执行器
     *
     * @param xxlJobGroup
     */
    private void registerJobHandler(XxlJobGroup xxlJobGroup) {
        log.debug("register job handler {}", xxlJobGroup);
        String url = xxlJobConfig.getBaseUrl() + "/jobgroup/save";
        Map<String, Object> params = objectToMap(xxlJobGroup);
        HttpResponse execute = doRequest("registerJobHandler", url, params);
        if (!success(execute)) {
            log.error("registerJobHandler fail resp :{}", execute);
        }
    }

    private HttpResponse doRequest(String key, String url, Map<String, Object> params) {
        if (StringUtils.isAnyBlank(xxlJobConfig.getBaseUrl(), xxlJobConfig.getUserName(), xxlJobConfig.getPassword())) {
            log.error("xxljob配置异常，无法使用，请检查配置");
            return null;
        }
        Map<String, List<String>> requestHeaders = loginAndBuildHeader();
        HttpRequest form = HttpRequest.post(url).header(requestHeaders).form(params);
        log.debug("{} req:{}", key, form);
        HttpResponse execute = form.execute();
        log.debug("{} resp:{}", key, execute);
        return execute;
    }

    private List<XxlJobGroup> getHandlerList(String appName) {
        log.debug("getHandlerList {}", appName);
        String url = xxlJobConfig.getBaseUrl() + "/jobgroup/pageList";
        Map<String, Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("length", "10000");
        if (StringUtils.isNotEmpty(appName)) {
            params.put("appname", appName);
        }
        HttpResponse execute = doRequest("getHandlerList", url, params);
        if (!success(execute)) {
            log.error("getHandlerList fail resp :{}", execute);
            return Collections.emptyList();
        }
        String body = execute.body();
        Map<String, Object> resp = decode(body, new TypeReference<Map<String, Object>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        if (resp.get("data") != null) {
            try {
                return decode(encode(resp.get("data")), new TypeReference<List<XxlJobGroup>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                });
            } catch (Exception exception) {
                log.error("getHandlerList fail resp json error:", exception);
                throw new BaseException(BaseExceptionCode.SYSTEM_ERROR);
            }
        } else {
            log.error("getHandlerList fail resp :{}", execute);
            throw new BaseException(BaseExceptionCode.SYSTEM_ERROR);
        }
    }

    public void updateJonHandler(XxlJobGroup xxlJobGroup) {
        log.debug("update job handler {}", xxlJobGroup.toString());
        String url = xxlJobConfig.getBaseUrl() + "/jobgroup/update";
        Map<String, Object> params = objectToMap(xxlJobGroup);
        HttpResponse execute = doRequest("updateJonHandler", url, params);
        if (!success(execute)) {
            log.error("updateJonHandler fail resp :{}", execute);
        }
    }

    /**
     * 注册任务
     *
     * @param info
     * @return
     */
    private int registerJob(XxlJobInfo info) {
        log.info("register job:{}", info.toString());
        String url = xxlJobConfig.getBaseUrl() + "/jobinfo/add";
        Map<String, Object> params = objectToMap(info);
        HttpResponse execute = doRequest("registerJob", url, params);
        if (!success(execute)) {
            log.error("registerJob fail resp :{}", execute);
        }
        String body = execute.body();
        Map<String, Object> resp = decode(body, new TypeReference<Map<String, Object>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        return Integer.parseInt(resp.get("content").toString());
    }

    public void registerJob(String name, String cron, String clazz) {
        //判断是否在其他节点已经注册任务
        XxlJobGroup xxlJobGroup = this.getXxlJobGroupByName();
        if (Objects.isNull(xxlJobGroup)) {
            log.info("xxlJobGroup为空,不能注册xxlJob");
            return;
        }
        List<XxlJobInfo> jobList = getJobList(xxlJobGroup.getId());
        boolean isExist = false;
        for (XxlJobInfo job : jobList) {
            String executorHandler = job.getExecutorHandler();
            if (Objects.equals(name, executorHandler)) {
                log.info("已有服务注册任务 {},不进行再次注册", name);
                isExist = true;
                break;
            }
        }
        if (isExist) {
            return;
        }
        // 查询是否需要注册JobHandler
        XxlJobInfo info = XxlJobInfo.builder()
                .jobGroup(xxlJobGroup.getId())
                .jobDesc("auto registerJob " + clazz)
                .author(SpringUtil.getApplicationName())
                .scheduleConf(cron)
                .executorHandler(name)
                .executorTimeout(5)
                .executorFailRetryCount(3)
                .build();
        log.info("=====注册 {} 定时任务 信息:{}", name, info);
        int jobId = registerJob(info);
        // 启动xxlJob
        startJob(jobId);
        log.info("=====注册 {} 定时任务jobId = {}", name, jobId);
    }

    /**
     * 获取所有任务
     *
     * @return
     */
    private List<XxlJobInfo> getJobList(int jobGroupId) {
        String url = xxlJobConfig.getBaseUrl() + "/jobinfo/pageList";
        Map<String, Object> params = new HashMap<>();
        params.put("start", "0");
        params.put("length", "10000");
        params.put("jobGroup", String.valueOf(jobGroupId));
        params.put("triggerStatus", "-1");
        HttpResponse execute = doRequest("getJobList", url, params);
        if (!success(execute)) {
            log.error("getJobList fail resp :{}", execute);
            return Collections.emptyList();
        }
        String body = execute.body();
        Map<String, Object> resp = decode(body, new TypeReference<Map<String, Object>>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        if (resp.get("data") != null) {
            try {
                return decode(encode(resp.get("data")), new TypeReference<List<XxlJobInfo>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                });
            } catch (Exception exception) {
                log.error("getJobList fail resp json error:", exception);
                throw new BaseException(BaseExceptionCode.SYSTEM_ERROR);
            }
        } else {
            log.error("getJobList fail resp :{}", execute);
            throw new BaseException(BaseExceptionCode.SYSTEM_ERROR);
        }
    }

    private void startJob(int id) {
        log.debug("start job ,id :{}", id);
        String url = xxlJobConfig.getBaseUrl() + "/jobinfo/start";
        Map<String, Object> params = new HashMap<>();
        params.put("id", String.valueOf(id));
        HttpResponse execute = doRequest("startJob", url, params);
        if (!success(execute)) {
            log.error("startJob resp :{}", execute);
        }

    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @
     */
    private static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            try {
                Object value = field.get(obj);
                if (value != null) {
                    map.put(fieldName, value.toString());
                }
            } catch (Exception exception) {
                log.error("xxl job field error", exception);
            }

        }
        return map;
    }


    private Map<String, List<String>> loginAndBuildHeader() {
        String url = xxlJobConfig.getBaseUrl() + "/login";
        Map<String, Object> params = new HashMap<>();
        params.put("userName", xxlJobConfig.getUserName());
        params.put("password", xxlJobConfig.getPassword());

        HttpRequest form = HttpRequest.post(url).contentType(ContentType.FORM_URLENCODED.getValue()).form(params);
        log.debug("request xxl job login request:{}", form);
        HttpResponse execute = form.execute();
        log.debug("request xxl job login resp :{} ", execute);
        if (Objects.equals(execute.getStatus(), 200)) {
            List<String> cookies = execute.headerList("Set-Cookie");
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Cookie", cookies);
            headers.put(Header.CONTENT_TYPE.getValue(), Collections.singletonList(ContentType.FORM_URLENCODED.getValue()));
            return headers;
        } else {
            log.error("request xxl job login fail resp :{}", execute);
            throw new BaseException(BaseExceptionCode.SYSTEM_ERROR);
        }
    }

    private boolean success(HttpResponse response) {
        return Objects.nonNull(response) && Objects.equals(response.getStatus(), 200);
    }


    private XxlJobGroup getXxlJobGroupByName() {
        // 查询是否需要注册JobHandler
        String appName = SpringUtil.getApplicationName();
        List<XxlJobGroup> jobGroupList = getHandlerList(appName);
        if (jobGroupList.isEmpty()) {
            // 注册xxlJobHelder
            XxlJobGroup xxlJobGroup = new XxlJobGroup();
            xxlJobGroup.setAppname(appName);
            xxlJobGroup.setTitle(appName);
            registerJobHandler(xxlJobGroup);
            jobGroupList = getHandlerList(appName);
        } else {
            updateJonHandler(jobGroupList.get(0));
        }
        return jobGroupList.stream().findFirst().orElse(null);
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);


    private static <T> String encode(T t) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(t);
    }

    private static <T> T decode(String json, TypeReference<T> valueTypeRef) {
        try {
            return OBJECT_MAPPER.readValue(json, valueTypeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
