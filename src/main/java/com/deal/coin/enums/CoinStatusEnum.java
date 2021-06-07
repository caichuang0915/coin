package com.deal.coin.enums;

/**
 * 记录状态
 * @author caichuang
 */
public enum CoinStatusEnum {

    HOLD(1,"还在持有"),
    SALED(2,"已经卖出"),
    ;
    private Integer type;
    private String desc;
    private CoinStatusEnum(Integer type, String desc){
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