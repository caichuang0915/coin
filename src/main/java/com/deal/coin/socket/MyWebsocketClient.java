package com.deal.coin.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deal.coin.enums.CoinInfoEnum;
import com.deal.coin.service.DealCoinService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;

/**
 * @Author: caichuang
 * @Date: 2021/6/5 11:38
 */
@Component
public class MyWebsocketClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MyWebsocketClient.class);

	@Autowired
	private DealCoinService dealCoinService;

	@Autowired
	private RedissonClient redissonClient;

	public void startListen() throws Exception{
		WebSocketClient client = new StandardWebSocketClient();
		String url = "wss://stream.yshyqxx.com/stream";
		client.doHandshake(new AbstractWebSocketHandler() {

			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				//链接成功后发送消息
				session.sendMessage(new TextMessage("{\"method\":\"SUBSCRIBE\",\"params\":[\"dotusdt@aggTrade\",\"btcusdt@aggTrade\",\"eosusdt@aggTrade\",\"ethusdt@aggTrade\",\"filusdt@aggTrade\"],\"id\":1}"));
			}

			@Override
			public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
				Object result = message.getPayload();
				JSONObject jsonObject = JSON.parseObject(result.toString());
				try {
					Object stream = jsonObject.get("stream");
					String name = CoinInfoEnum.getName(stream.toString());
					Object data = jsonObject.getJSONObject("data").get("p");
					// 同时只能处理一条消息 期间的其它消息忽略
					String key = "coin:price:deal:"+name;
                    RLock lock = redissonClient.getLock(key);
                    if(lock.tryLock()){
                        dealCoinService.dealCoinPrice(name,data.toString());
                    }
				}catch (Exception e){
					LOGGER.error("查询价格错误");
				}
			}
		}, new WebSocketHttpHeaders(),new URI(url));
		System.in.read();
	}


}
