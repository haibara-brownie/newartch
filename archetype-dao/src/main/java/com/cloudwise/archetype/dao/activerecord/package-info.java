// CHECKSTYLE:OFF
/**
 * 统一管理数据库实体类model
 *
 * @see com.cloudwise.archetype.dao.mapper
 * @see com.cloudwise.archetype.dao.activerecord.ExampleModel
 * 1、特殊属性注解参看ExampleModel，增删改时无需对isDeleted和插入及更新时间赋值
 * 2、实现原理参看BdpMetaObjectHandler
 */
package com.cloudwise.archetype.dao.activerecord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
class ExampleModel implements Serializable {
    private static final long serialVersionUID = 4138013676949458090L;

    private String exampleName;

}