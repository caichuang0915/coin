package com.deal.coin.controller;

import com.alibaba.fastjson.JSON;
import com.deal.coin.service.DingDingService;
import com.deal.coin.util.OkHttpUtil;
import com.deal.coin.vo.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Caichuang
 * @Date 2021/6/5 9:52 PM
 * @Description
 */
@RestController
public class TestController {

    @Autowired
    private OkHttpUtil okHttpUtil;

    @Autowired
    private DingDingService dingDingService;

    @RequestMapping("/test")
    public String test(){
        return "111";
    }

}
