package com.cloudwise.archetype.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwise.archetype.dao.activerecord.Air;
import com.cloudwise.archetype.dao.activerecord.AirOrders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author haibarabrownie
* @description 针对表【AirOrders】的数据库操作Service
* @createDate 2024-07-11 22:07:49
*/
public interface AirOrdersService extends IService<AirOrders> {
	
	void createOrder(String json);
	Page<AirOrders> selectByPage(long current, long size, String brand);
	Page<AirOrders> selectByPage(long current, long size);
}
