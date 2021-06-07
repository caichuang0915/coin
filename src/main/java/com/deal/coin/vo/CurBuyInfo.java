package com.deal.coin.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author Caichuang
 * @Date 2021/6/5 10:04 PM
 * @Description -
 */
@Data
public class CurBuyInfo {
    private String coinName;
    private BigDecimal buyPrice;
    private BigDecimal buyNum;
    private BigDecimal cash;
    private BigDecimal coinNum;
    private BigDecimal principal;
}
