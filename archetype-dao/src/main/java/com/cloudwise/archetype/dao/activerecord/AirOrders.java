package com.cloudwise.archetype.dao.activerecord;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName AirOrders
 */
@TableName(value ="air_orders")
@Data
public class AirOrders implements Serializable {
    /**
     * 
     */
    @TableId(value = "oid")
    private String oid;

    /**
     * 
     */
    @TableField("aid")
    private String aid;

    /**
     * 
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 
     */
    @TableField("ocreate_id")
    private String ocreate_id;

    /**
     * 
     */
    @TableField("ocreate_time")
    private Date ocreate_time;
    
    private static final long serialVersionUID = 1L;
}