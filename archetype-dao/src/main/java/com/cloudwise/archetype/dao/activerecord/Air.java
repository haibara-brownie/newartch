package com.cloudwise.archetype.dao.activerecord;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName Air
 */
@TableName(value ="air")
@Data
public class Air implements Serializable {
    /**
     * 
     */
    @TableId(value = "aid")
    private String aid;

    /**
     * 
     */
    @TableField("brand")
    private String brand;

    /**
     * 
     */
    @TableField("color")
    private String color;

    /**
     * 
     */
    @TableField("level")
    private Integer level;

    /**
     * 
     */
    @TableField("model")
    private String model;

    /**
     * 
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 
     */
    @TableField("create_id")
    private String create_id;
    
    @TableField("create_name")
    private String create_name;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date update_time;
    
    private static final long serialVersionUID = 1L;
}