package com.cloudwise.archetype.api.controller; /*
 *Administrator haibarabrownie
 *Create on 2024/7/10 上午10:31
 */

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwise.archetype.api.service.AirOrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.cloudwise.archetype.dao.activerecord.AirOrders;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class AirOrdersController {

    private final AirOrdersService airOrdersService;

    /**
     * 添加数据
     */
    @PostMapping("/backadd")
    void addAirOrders(@RequestBody String json) {
        airOrdersService.createOrder(json);
    }

    /**
     * 进行分页查询json格式
     *
     * @return 所有的车辆信息
     */
    @PostMapping("/backlist")
    public Page<AirOrders> listAirByPage(@RequestBody Page<AirOrders> page) {
        long current = page.getCurrent();
        long size = page.getSize();
        return airOrdersService.selectByPage(current, size);
    }

    /**
     * 列表查询全部订单信息
     */
    @GetMapping("/list")
    public List<AirOrders> list() {
        return airOrdersService.list();
    }

    /**
     * json传参数分页查询
     */
    @PostMapping("/pageJsonParam")
    public Page<AirOrders> getByPageParam(@RequestBody String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        long current = jsonObject.getLongValue("current");
        long size = jsonObject.getIntValue("size");
        String brand = jsonObject.getString("brand");
        return airOrdersService.selectByPage(current, size, brand);
    }
}
