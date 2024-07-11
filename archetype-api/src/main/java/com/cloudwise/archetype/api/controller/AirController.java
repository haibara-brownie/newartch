package com.cloudwise.archetype.api.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwise.archetype.api.service.AirService;
import com.cloudwise.archetype.dao.activerecord.Air;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.cloudwise.douc.facade.UserV2DubboFacade;
import com.cloudwise.douc.dto.DubboUserIdAccountIdRequest;
import java.util.List;

/**
 * @author haibarabrownie
 */

@RestController
@RequestMapping("/air")
@RequiredArgsConstructor
public class AirController {
    private final AirService airService;
    
    @Autowired
    UserV2DubboFacade userV2DubboFacade;

    /**
     * 添加数据
     */
    // @PostMapping("/add")
    // void addAir(@RequestBody Air air, @RequestHeader String userId, @RequestHeader String accountId) {
    //     DubboUserIdAccountIdRequest dubboUserIdAccountIdRequest = new DubboUserIdAccountIdRequest();
    //     dubboUserIdAccountIdRequest.setAccountId(Long.valueOf(accountId));
    //     dubboUserIdAccountIdRequest.setUserId(Long.valueOf(userId));
    //     String nameParse = userV2DubboFacade.getUserDetailInfoById(dubboUserIdAccountIdRequest).toString();
    //
    //     JSONObject jsonObject = JSON.parseObject(nameParse);
    //     String data = jsonObject.getString("data");
    //     JSONObject jsonObject1 = JSON.parseObject(data);
    //     String name = jsonObject1.getString("name");
    //
    //     air.setCreateName(name);
    //     air.setCreate_id(userId);
    //     airService.save(air);
    // }
    
    /**
     * 添加数据
     */
    @PostMapping("/backadd")
    void addAir(@RequestBody Air air) {
        air.setCreate_name("1234");
        airService.save(air);
    }
    
    @PostMapping("/getbrand/{id}")
    public String getBrandById(@PathVariable String id) {
        return airService.getById(id).getBrand();
    }
    

    /**
     * 根据id查询空调信息
     */
    @PostMapping("/backsearch/{id}")
    public Air getById(@PathVariable String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return airService.getById(id);
    }

    /**
     * 进行分页查询json格式
     *
     * @return 所有的空调信息
     */
    @PostMapping("/backlist")
    public Page<Air> listAirByPage(@RequestBody Page<Air> page) {
        long current = page.getCurrent();
        long size = page.getSize();
        return airService.selectByPage(current, size);
    }


    /**
     * json传参数分页查询
     */
    @PostMapping("/pageJsonParam")
    public Page<Air> getByPageParam(@RequestBody String json) {
        if (json == null) {
            throw new IllegalArgumentException("page and param is null");
        }
        JSONObject jsonObject = JSON.parseObject(json);
        long current = jsonObject.getLongValue("current");
        long size = jsonObject.getIntValue("size");
        String brand = jsonObject.getString("brand");
        return airService.selectByPage(current, size, brand);
    }


    /**
     * 列表查询全部车辆信息
     */
    @GetMapping("/list")
    public List<Air> list() {
        return airService.list();
    }

    /**
     * 根据id删除车辆信息
     */
    @PostMapping("/backdelete/{id}")
    public boolean deleteById(@PathVariable String id) {
        return airService.removeById(id);
    }

    /**
     * 更新车辆信息
     */
    @PostMapping("/backupdate")
    public boolean updateAir(@RequestBody Air air) {
        return airService.updateById(air);
    }
}
