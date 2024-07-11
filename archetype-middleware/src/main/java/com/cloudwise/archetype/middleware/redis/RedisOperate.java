package com.cloudwise.archetype.middleware.redis;

import cn.hutool.extra.spring.SpringUtil;
import com.cloudwise.cache.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wangpei
 * @date 2023-10-17
 * redis常用操作
 */
@Service
public class RedisOperate {
    /**
     * 普通缓存处理
     * private RedisUtils redisUtils;
     * 注入前提是启动类增加了相关注解(且配置正常)
     * 常规缓存(开启@EnableLettuceRedis)
     * 订阅发布@EnableSubcribe
     * redisson分布式锁@EnableRedisson
     */
    private void cache() {
        RedisUtils redisUtils = SpringUtil.getBean(RedisUtils.class);
        // 获取某个key对应的value值
        Object test1 = redisUtils.get("test1");
        // 设置缓存,指定过期时间及单位
        redisUtils.set("test2", "111", 1000, TimeUnit.MINUTES);
        // 批量设置缓存,单位秒
        Map<String, String> map = new HashMap<>();
        redisUtils.batchSet(map, 1000);
        // 模糊搜索rediskey，传参key与搜索步长
        redisUtils.scan("test3", 1000L);
        // 判断key是否存在
        redisUtils.hasKey("test4");
        // 删除key,也可传list
        redisUtils.del("test5");
        // 指定过期时间
        redisUtils.expire("test6", 30, TimeUnit.SECONDS);
        // 获取过期时间,单位秒
        redisUtils.getExpire("test7");
        // 设置hash表
        redisUtils.hmset("test8", map, 3000, TimeUnit.SECONDS);
        // 获取hash表
        redisUtils.hmget("test8");
        // 获取hash表某个值
        redisUtils.hget("test8", "item");
        // 其余操作命令与redis官方操作类似
    }
}
