package com.deal.coin.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Caichuang
 * @Date 2021/6/5 10:04 PM
 * @Description -
 */
@Data
public class BuyListReqVo {

    private String coinName;

    private Integer type;

    private Integer status;

}
