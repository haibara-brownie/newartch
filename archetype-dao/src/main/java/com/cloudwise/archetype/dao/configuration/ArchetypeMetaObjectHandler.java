package com.cloudwise.archetype.dao.configuration;

import cn.hutool.core.date.SystemClock;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.cloudwise.archetype.dao.constant.DbConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author jiayongming
 */
@Slf4j
@Component
public class ArchetypeMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        final long currentTimeMillis = SystemClock.now();
        this.strictInsertFill(metaObject, DbConstants.LOGIC_DELETE_FIELD, Integer.class, DbConstants.LOGIC_NOT_DELETE_VALUE);
        this.strictInsertFill(metaObject, DbConstants.CREATE_TIME_FIELD, Long.class, currentTimeMillis);
        this.strictInsertFill(metaObject, DbConstants.MODIFY_TIME_FIELD, Long.class, currentTimeMillis);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        final long currentTimeMillis = SystemClock.now();
        this.strictUpdateFill(metaObject, DbConstants.CREATE_TIME_FIELD, Long.class, currentTimeMillis);
        this.strictUpdateFill(metaObject, DbConstants.MODIFY_TIME_FIELD, Long.class, currentTimeMillis);
    }

    /**
     * 严格模式填充策略,有没有值均强制覆盖,如果提供的值为null不填充
     *
     * @param metaObject metaObject meta object parameter
     * @param fieldName  java bean property name
     * @param fieldVal   java bean property value of Supplier
     * @return this
     * @since 3.3.0
     */
    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        Object obj = fieldVal.get();
        if (Objects.nonNull(obj)) {
            metaObject.setValue(fieldName, obj);
        }
        return this;
    }
}