package com.cloudwise.archetype.dao.constant;

public class DbConstants {

    /**
     * 逻辑删除全局属性名
     */
    public static final String LOGIC_DELETE_FIELD = "isDeleted";
    /**
     * 逻辑未删除全局值（默认 0 表示未删除、1、表示已删除）
     */
    public static final int LOGIC_NOT_DELETE_VALUE = 0;
    public static final int LOGIC_DELETE_VALUE = 1;
    /**
     * 创建时间属性名
     */
    public static final String CREATE_TIME_FIELD = "createTime";
    /**
     * 更新时间属性名
     */
    public static final String MODIFY_TIME_FIELD = "modifyTime";
}
