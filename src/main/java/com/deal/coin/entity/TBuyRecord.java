package com.deal.coin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@TableName(value = "t_buy_record",schema = "coin")
public class TBuyRecord implements Serializable {

    private static final long serialVersionUID = 1L;



    @TableField("id")
    private Long id;

    @TableField("coin_name")
    private String coinName;

    @TableField("buy_price")
    private BigDecimal buyPrice;

    @TableField("buy_num")
    private BigDecimal buyNum;

    @TableField("cash")
    private BigDecimal cash;

    @TableField("coin_num")
    private BigDecimal coinNum;

    @TableField("principal")
    private BigDecimal principal;

    @TableField("create_time")
    private Date createTime;

    @TableField("type")
    private Integer type;

    @TableField("status")
    private Integer status;
}
