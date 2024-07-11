package com.cloudwise.archetype.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloudwise.archetype.dao.activerecord.Air;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author haibarabrownie
* @description 针对表【Air】的数据库操作Service
* @createDate 2024-07-11 22:04:07
*/
public interface AirService extends IService<Air> {
	
	Page<Air> selectByPage(long current, long size, String brand);
	
	Page<Air> selectByPage(long current, long size);
}
