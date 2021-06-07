package com.deal.coin.vo;

import lombok.Data;

/**
 * @Author Caichuang
 * @Date 2021/6/5 10:04 PM
 * @Description -
 */
@Data
public class TextMessage {
    private String msgtype;
    private MsgContent text;

    public TextMessage(){}

    public TextMessage(String message){
        this.msgtype = "text";
        this.text = new MsgContent(message);
    }

    @Data
    private class MsgContent {
        private String content;
        MsgContent(String content){
            this.content = content;
        }
    }
}
