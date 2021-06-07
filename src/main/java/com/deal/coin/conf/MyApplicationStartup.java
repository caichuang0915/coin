package com.deal.coin.conf;

import com.deal.coin.socket.MyWebsocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * @Author Caichuang
 * @Date 2021/6/5 11:01 PM
 * @Description
 */
public class MyApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyApplicationStartup.class);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext ac = contextRefreshedEvent.getApplicationContext();
        MyWebsocketClient client = ac.getBean(MyWebsocketClient.class);
        try {
            client.startListen();
        } catch (Exception e) {
            LOGGER.error("启动币价监听失败。。。");
        }
    }
}
