package com.cloudwise.archetype.middleware.xxljob.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xuxueli on 16/9/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class XxlJobGroup {
    private Integer id;
    private String appname;
    private String title;
    private int addressType;        // 执行器地址类型：0=自动注册、1=手动录入
    private String addressList;     // 执行器地址列表，多地址逗号分隔(手动录入)
    private Date updateTime = new Date();

    // registry list
    private List<String> registryList;  // 执行器地址列表(系统注册)

    public List<String> getRegistryList() {
        if (StringUtils.isNotBlank(addressList)) {
            registryList = Arrays.stream(addressList.split(",")).collect(Collectors.toList());
        }
        return registryList;
    }

}
