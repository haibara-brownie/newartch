package com.cloudwise.archetype.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloudwise.archetype.api.service.AirService;
import com.cloudwise.archetype.dao.activerecord.Air;
import com.cloudwise.archetype.dao.activerecord.AirOrders;
import com.cloudwise.archetype.dao.mapper.AirMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author haibarabrownie
* @description 针对表【Air】的数据库操作Service实现
* @createDate 2024-07-11 22:04:07
*/
@Service
public class AirServiceImpl extends ServiceImpl<AirMapper, Air> implements AirService {
	
	@Autowired
	private AirMapper airMapper;
	@Override
	public Page<Air> selectByPage(long current, long size, String brand) {
		Page<Air> rowPage = new Page<>(current, size);
		QueryWrapper<Air> queryWrapper = new QueryWrapper<>();
		queryWrapper.like("brand", brand);
		rowPage = this.baseMapper.selectPage(rowPage, queryWrapper);
		return rowPage;
	}
	
	@Override
	public Page<Air> selectByPage(long current, long size) {
		Page<Air> page = new Page<>(current, size);
		LambdaQueryWrapper<Air> queryWrapper = Wrappers.lambdaQuery();
		return airMapper.selectPage(page, queryWrapper);
	}
}




