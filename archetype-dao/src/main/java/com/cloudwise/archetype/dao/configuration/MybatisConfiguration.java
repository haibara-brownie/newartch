package com.cloudwise.archetype.dao.configuration;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.cloudwise.archetype.dao.constant.DbConstants;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;

/**
 * @author jiayongming
 */
@MapperScan(basePackages = {"com.cloudwise.archetype.dao.mapper"},
        sqlSessionTemplateRef = "mysqlDataSourceSqlSessionTemplate")
@Configuration
public class MybatisConfiguration {

    @Resource
    private HikariDataSource hikariDataSource;

    @Resource
    private ArchetypeMetaObjectHandler archetypeMetaObjectHandler;

    @Bean
    @Primary
    public SqlSessionFactory mysqlDataSourceSqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        mybatisSqlSessionFactoryBean.setDataSource(hikariDataSource);
        Interceptor[] interceptors;
        interceptors = ArrayUtils.toArray(mybatisPlusInterceptor());
        mybatisSqlSessionFactoryBean.setPlugins(interceptors);

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setBanner(false);
        //配置dbConfig
        globalConfig.setDbConfig(dbConfig());
        //配置填充器
        globalConfig.setMetaObjectHandler(archetypeMetaObjectHandler);
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);

        return mybatisSqlSessionFactoryBean.getObject();
    }

    @Bean
    @Primary
    public DataSourceTransactionManager mysqlDataSourceTransactionManager() {
        return new DataSourceTransactionManager(hikariDataSource);
    }

    @Bean
    @Primary
    public SqlSessionTemplate mysqlDataSourceSqlSessionTemplate(SqlSessionFactory mysqlDataSourceSqlSessionFactory) {
        return new SqlSessionTemplate(mysqlDataSourceSqlSessionFactory);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    @Bean
    public GlobalConfig.DbConfig dbConfig() {
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setLogicNotDeleteValue(String.valueOf(DbConstants.LOGIC_NOT_DELETE_VALUE));
        dbConfig.setLogicDeleteValue(String.valueOf(DbConstants.LOGIC_DELETE_VALUE));
        dbConfig.setLogicDeleteField(DbConstants.LOGIC_DELETE_FIELD);
        return dbConfig;
    }
}