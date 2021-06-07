package com.deal.coin.enums;

/**
 * 记录类型
 * @author caichuang
 */
public enum CoinTypeEnum {

    BUY(1,"买入"),
    SALE(2,"卖出"),
    ;
    private Integer type;
    private String desc;
    private CoinTypeEnum(Integer type, String desc){
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}