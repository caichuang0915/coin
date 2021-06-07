package com.deal.coin.service;

import org.springframework.stereotype.Service;

/**
 * @Author: caichuang
 * @Date: 2021/6/5 17:07
 */
@Service
public interface DingDingService {

	/**
	 * 发送消息到钉钉
	 * @param message -
	 */
	void sendMessage(String message);

}
