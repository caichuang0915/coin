package com.deal.coin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deal.coin.entity.TBuyRecord;
import com.deal.coin.enums.CoinStatusEnum;
import com.deal.coin.enums.CoinTypeEnum;
import com.deal.coin.service.DealCoinService;
import com.deal.coin.service.DingDingService;
import com.deal.coin.service.ITBuyRecordService;
import com.deal.coin.util.OkHttpUtil;
import com.deal.coin.vo.BuyListReqVo;
import com.deal.coin.vo.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author Caichuang
 * @Date5 11:27 PM
 * @Description
 */
@Service
@Slf4j
public class DingDingServiceImpl implements DingDingService {

    @Autowired
    private OkHttpUtil okHttpUtil;

    private static final String WEB_HOOK = "https://oapi.dingtalk.com/robot/send?access_token=427f18f331e42828e376736637eb87aaf2045f5449acfb3091d92c4e530a88eb";

    @Override
    public void sendMessage(String message){
        TextMessage text = new TextMessage(message);
        okHttpUtil.postJsonParams(WEB_HOOK, JSON.toJSONString(text));
    }

}
