package com.cloudwise.archetype.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwise.archetype.api.service.AirOrdersService;
import com.cloudwise.archetype.dao.activerecord.Air;
import com.cloudwise.archetype.dao.activerecord.AirOrders;
import com.cloudwise.archetype.dao.mapper.AirOrdersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
* @author haibarabrownie
* @description 针对表【AirOrders】的数据库操作Service实现
* @createDate 2024-07-11 22:07:49
*/
@Service
@RequiredArgsConstructor
public class AirOrdersServiceImpl extends ServiceImpl<AirOrdersMapper, AirOrders> implements AirOrdersService {
	
	private final AirOrdersMapper airOrdersMapper;
	private final KafkaTemplate<String,String> kafkaTemplate;
	
	@Override
	public void createOrder(String json) {
		kafkaTemplate.send("air",json);
	}
	
	@Override
	public Page<AirOrders> selectByPage(long current, long size, String brand) {
		Page<AirOrders> rowPage = new Page<>(current, size);
		QueryWrapper<AirOrders> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("brand", brand);
		rowPage = this.baseMapper.selectPage(rowPage, queryWrapper);
		return rowPage;
	}
	
	@Override
	public Page<AirOrders> selectByPage(long current, long size) {
		Page<AirOrders> page = new Page<>(current, size);
		LambdaQueryWrapper<AirOrders> queryWrapper = Wrappers.lambdaQuery();
		return airOrdersMapper.selectPage(page, queryWrapper);
	}
}




