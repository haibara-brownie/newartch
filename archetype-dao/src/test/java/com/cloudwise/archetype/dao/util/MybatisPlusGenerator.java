package com.cloudwise.archetype.dao.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;

public class MybatisPlusGenerator {

    /**
     * 数据库连接
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(
            "jdbc:mysql://127.0.0.1:3306/cw_archetype", "root", "root");

    public static void main(String[] args) {
        Class<?> clazz = MybatisPlusGenerator.class; // 替换为当前类的类对象
        URL url = clazz.getResource("");
        String currentDirectory = url.getPath();
        int index = currentDirectory.indexOf("target"); // 获取"target"字符串在原字符串中的位置
        if (index != -1) { // 如果找到了"target"
            currentDirectory = currentDirectory.substring(0, index); // 截取"target"之前的部分
        }
        String metadataPrefix = currentDirectory + "src/main/java/com/cloudwise/archetype/dao/";
        String servicePrefix = "";
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                .globalConfig(builder -> builder.author("jamin.jia") // 设置作者
                        .disableOpenDir()
                        .enableSwagger() // 开启 swagger 模式
                        .dateType(DateType.ONLY_DATE))
                .packageConfig(builder -> {
                    builder.parent("com.cloudwise.archetype.dao") // 设置父包名
                            .moduleName(StringUtils.EMPTY)
                            .pathInfo(ImmutableMap.of(
                                    OutputFile.controller, StringUtils.EMPTY,
                                    OutputFile.xml, StringUtils.EMPTY,
                                    OutputFile.entity, metadataPrefix + "activerecord",
                                    OutputFile.mapper, metadataPrefix + "mapper",
                                    OutputFile.service, StringUtils.EMPTY,
                                    OutputFile.serviceImpl, StringUtils.EMPTY
                            ));
                })
                .strategyConfig(builder -> {
                    builder
                            .addInclude("archetype_test") // 设置需要生成的表名
                            //.addExclude("DATABASECHANGELOG", "DATABASECHANGELOGLOCK")
                            .entityBuilder().enableLombok()
                            .disableSerialVersionUID()
                            .enableTableFieldAnnotation()
                            .enableChainModel()
                            .fileOverride();
                    //.superClass(superClass)
                    //.addTableFills(tableFillList);
                })
                .templateConfig(builder -> {
                    new TemplateConfig.Builder()
                            .disable()
                            .build();
                })
                .templateEngine(new BeetlTemplateEngine())
                .execute();

    }
}
