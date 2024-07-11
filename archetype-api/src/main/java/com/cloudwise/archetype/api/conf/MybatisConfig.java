package com.cloudwise.archetype.api.conf;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
@Configuration
public class MybatisConfig {
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		// 初始化核心插件
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 添加分页插件
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
		return interceptor;
	}
}
