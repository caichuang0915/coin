package com.deal.coin.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录类型
 * @author caichuang
 */
public enum CoinInfoEnum {

    BTC("btcusdt@aggTrade","BTC"),
    ETH("ethusdt@aggTrade","ETH"),
    EOS("eosusdt@aggTrade","EOS"),
    DOT("dotusdt@aggTrade","DOT"),
    FIL("filusdt@aggTrade","FIL"),
    ;
    private String type;
    private String name;
    private CoinInfoEnum(String type, String name){
        this.type = type;
        this.name = name;
    }

    private static Map<String,String> map = new HashMap<>(5);

    static {
        for (CoinInfoEnum coinInfoEnum : CoinInfoEnum.values()) {
            map.put(coinInfoEnum.getType(),coinInfoEnum.getName());
        }
    }

    public static String getName(String type) {
        return map.get(type);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}