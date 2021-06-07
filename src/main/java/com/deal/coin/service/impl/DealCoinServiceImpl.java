package com.deal.coin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.deal.coin.entity.TBuyRecord;
import com.deal.coin.enums.CoinStatusEnum;
import com.deal.coin.enums.CoinTypeEnum;
import com.deal.coin.service.DealCoinService;
import com.deal.coin.service.DingDingService;
import com.deal.coin.service.ITBuyRecordService;
import com.deal.coin.vo.BuyListReqVo;
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
public class DealCoinServiceImpl implements DealCoinService {

    @Autowired
    private RedisTemplate<String,  String> redisTemplate;
    @Autowired
    private DingDingService dingDingService;
    @Autowired
    private ITBuyRecordService buyRecordService;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final BigDecimal dealRate = new BigDecimal(0.999);

    @Override
    public TBuyRecord getResentRecord(String coinName) {
        // 先从缓存取 缓存没有从数据库取 数据库没有则认为是第一次操作
        String redisKey = "coin:price:last:one:"+coinName;
        String s = redisTemplate.opsForValue().get(redisKey);
        if(StringUtils.isNotEmpty(s)){
            return JSONObject.parseObject(s,TBuyRecord.class);
        }
        // 从数据库查询
        return getRecentRecordFromDbAndCash(coinName);
    }

    private TBuyRecord getRecentRecordFromDbAndCash(String coinName){
        String redisKey = "coin:price:last:one:"+coinName;
        BuyListReqVo reqVo = new BuyListReqVo();
        reqVo.setCoinName(coinName);
        TBuyRecord recentBuy = buyRecordService.getRecentBuy(reqVo);
        redisTemplate.opsForValue().set(redisKey,JSON.toJSONString(recentBuy));
        return recentBuy;
    }

    @Override
    public void dealCoinPrice(String coinName, String price) {
        // 查询上次购入的价格
        TBuyRecord resentPrice = getResentRecord(coinName);
        BigDecimal curPrice = new BigDecimal(price);
        if(null != resentPrice){
            BigDecimal buyPrice = resentPrice.getBuyPrice();
            BigDecimal subtract = curPrice.subtract(buyPrice).divide(buyPrice,4,BigDecimal.ROUND_HALF_UP);
            log.info("最近一次操作 {} 价格为 {} 当前价格为 {} 波动为 {}",coinName,buyPrice,curPrice,subtract);
            if(subtract.compareTo(new BigDecimal("0.008"))==1){
                log.info("{} 价格涨幅超过0.8% 卖出 当前价格 {}",coinName,price);
                saleCoin(coinName,curPrice,resentPrice);
                return;
            }
            if(subtract.compareTo(new BigDecimal("-0.008"))==-1){
                log.info("{} 价格跌幅超过0.8% 买入 当前价格 {}",coinName,price);
                buyCoin(coinName,curPrice,resentPrice);
                return;
            }
            return;
        }
        buyCoin(coinName,curPrice,null);
    }


    private void saleCoin(String coinName,BigDecimal curPrice,TBuyRecord resentPrice){
        if(null==resentPrice || resentPrice.getCoinNum().compareTo(new BigDecimal("0"))==1){
            String message = String.format("时间【%s】【%s】价格【%s】涨幅超过0.8%% 当前无份额 观望。。。",
                    simpleDateFormat.format(new Date()),coinName, curPrice.toString());
            log.info(message);
            dingDingService.sendMessage(message);
            return;
        }
        TBuyRecord curSale = new TBuyRecord();
        // 最近一次买入的数量为此次卖出的数量
        BigDecimal buyNum = resentPrice.getBuyNum();
        if(buyNum.compareTo(resentPrice.getCoinNum())==1){
            buyNum = resentPrice.getCoinNum();
        }
        // 此次卖出的收益
        BigDecimal multiply = curPrice.multiply(buyNum).multiply(dealRate);

        // 记录此次的卖出
        curSale.setBuyNum(buyNum);
        curSale.setBuyPrice(curPrice);
        curSale.setCash(resentPrice.getCash().add(multiply));
        curSale.setCoinNum(resentPrice.getCoinNum().subtract(buyNum));
        curSale.setCreateTime(new Date());
        curSale.setPrincipal(resentPrice.getPrincipal());
        curSale.setType(CoinTypeEnum.SALE.getType());
        curSale.setStatus(CoinStatusEnum.SALED.getType());
        curSale.setCoinName(coinName);
        buyRecordService.save(curSale);

        // 修改上次的买入记录
        resentPrice.setStatus(CoinStatusEnum.SALED.getType());
        buyRecordService.updateById(resentPrice);

        // 更新上次买入缓存
        getRecentRecordFromDbAndCash(coinName);

        // 发送消息到钉钉
        // 总投入
        BigDecimal prin = curSale.getPrincipal();
        // 总值
        BigDecimal total = curSale.getCoinNum().multiply(curPrice).add(curSale.getCash());
        // 收益率
        BigDecimal divide = total.subtract(prin).multiply(new BigDecimal(100)).divide(prin, 3, BigDecimal.ROUND_HALF_UP);
        String message = String.format("时间【%s】【%s】价格【%s】上次买入价格【%s】涨幅超过0.8%% 卖出【%s】个币 当前持币数量 【%s】持币当前价值【%s】当前现金【%s】当前总值【%s】总投入【%s】 收益率【%s】%%",
                simpleDateFormat.format(new Date()),coinName, curPrice.toString(),resentPrice.getBuyPrice().toString(),buyNum.toString(),
                curSale.getCoinNum().toString(),
                curSale.getCoinNum().multiply(curPrice).setScale(5, BigDecimal.ROUND_HALF_UP).toString(),curSale.getCash().setScale(5, BigDecimal.ROUND_HALF_UP).toString(),
                total.setScale(5, BigDecimal.ROUND_HALF_UP).toString(),curSale.getPrincipal().toString(),
                divide.toString());
        log.info(message);
        dingDingService.sendMessage(message);
    }

    private void buyCoin(String coinName,BigDecimal curPrice,TBuyRecord resentPrice){
        String redisKey = "coin:price:last:one:"+coinName;
        BigDecimal principal = new BigDecimal("100");
        BigDecimal buyNum = principal.multiply(dealRate).divide(curPrice, 5, BigDecimal.ROUND_HALF_UP);
        String message = null;
        if(null==resentPrice){
            String priceKey = "coin:price:first:"+coinName;
            // 记录当前价格
            String s = redisTemplate.opsForValue().get(priceKey);
            if(StringUtils.isEmpty(s)){
                redisTemplate.opsForValue().set(priceKey,curPrice.toString());
                return;
            }
            BigDecimal p = new BigDecimal(s);
            // 还在涨 刷新记录
            if(curPrice.compareTo(p)==1){
                log.info("刷新当前 {} 价格 {}",coinName,curPrice);
                redisTemplate.opsForValue().set(priceKey,curPrice.toString());
                return;
            }
            // 如果跌了 跌幅超过多少 则买入
            BigDecimal subtract = p.subtract(curPrice).divide(p,4,BigDecimal.ROUND_HALF_UP);
            if(subtract.compareTo(new BigDecimal("0.008"))!=1){
                log.info("{} 最高价格 {} 当前价格 {} 跌幅 {} 不够 继续等待",coinName,p.toString(),curPrice,subtract);
                return;
            }
            TBuyRecord saleAllInfo = getSaleAllInfo(coinName);
            log.info("买入{} 数据库存入记录",coinName);
            resentPrice = new TBuyRecord();
            resentPrice.setPrincipal(principal.add(saleAllInfo.getPrincipal()));
            resentPrice.setCoinNum(buyNum);
            resentPrice.setCash(saleAllInfo.getCash());
            resentPrice.setBuyPrice(curPrice);
            resentPrice.setBuyNum(buyNum);
            resentPrice.setCoinName(coinName);
            resentPrice.setCreateTime(new Date());
            resentPrice.setStatus(CoinStatusEnum.HOLD.getType());
            resentPrice.setType(CoinTypeEnum.BUY.getType());
            buyRecordService.save(resentPrice);
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(resentPrice));
            // 发送消息到钉钉
            // 总投入
            BigDecimal prin = resentPrice.getPrincipal();
            // 总值
            BigDecimal total = buyNum.multiply(curPrice).add(resentPrice.getCash());
            // 收益率
            BigDecimal divide = total.subtract(prin).multiply(new BigDecimal(100)).divide(prin, 3, BigDecimal.ROUND_HALF_UP);

            message = String.format("时间【%s】【%s】价格【%s】 买入100元 当前持币数量 【%s】持币当前价值【%s】当前现金【%s】总投入【%s】 收益率【%s】%%",
                    simpleDateFormat.format(new Date()),coinName, curPrice.toString(),buyNum.toString(),
                    buyNum.multiply(curPrice).setScale(5, BigDecimal.ROUND_HALF_UP).toString(),
                    resentPrice.getCash().setScale(5, BigDecimal.ROUND_HALF_UP),resentPrice.getPrincipal(),divide.toString()
            );
        }else {
            log.info("再次买入{} 再前一次的基础上修改",coinName);
            TBuyRecord curBuyInfo = new TBuyRecord();
            curBuyInfo.setBuyNum(buyNum);
            curBuyInfo.setBuyPrice(curPrice);
            curBuyInfo.setCoinName(coinName);
            curBuyInfo.setCash(resentPrice.getCash());
            curBuyInfo.setCoinNum(resentPrice.getCoinNum().add(buyNum));
            curBuyInfo.setPrincipal(resentPrice.getPrincipal().add(principal));
            curBuyInfo.setCreateTime(new Date());
            curBuyInfo.setStatus(CoinStatusEnum.HOLD.getType());
            curBuyInfo.setType(CoinTypeEnum.BUY.getType());
            buyRecordService.save(curBuyInfo);
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(curBuyInfo));

            // 发送消息到钉钉
            // 总投入
            BigDecimal prin = curBuyInfo.getPrincipal();
            // 总值
            BigDecimal total = curBuyInfo.getCoinNum().multiply(curPrice).add(curBuyInfo.getCash());
            // 收益率
            BigDecimal divide = total.subtract(prin).multiply(new BigDecimal(100)).divide(prin, 3, BigDecimal.ROUND_HALF_UP);


            message = String.format("时间【%s】【%s】价格【%s】上次买入价格【%s】跌幅超过0.8%% 买入100元 当前持币数量 【%s】持币当前价值【%s】当前现金【%s】当前总值【%s】总投入【%s】 收益率【%s】%%",
                    simpleDateFormat.format(new Date()),coinName, curPrice.toString(),resentPrice.getBuyPrice().toString(),
                    curBuyInfo.getCoinNum().setScale(5, BigDecimal.ROUND_HALF_UP).toString(),
                    curBuyInfo.getCoinNum().multiply(curPrice).setScale(5, BigDecimal.ROUND_HALF_UP).toString(),
                    curBuyInfo.getCash().setScale(5, BigDecimal.ROUND_HALF_UP).toString(),
                    total.setScale(5, BigDecimal.ROUND_HALF_UP).toString(),curBuyInfo.getPrincipal().toString(),
                    divide.toString());
        }
        log.info(message);
        dingDingService.sendMessage(message);
    }

    /**
     * 获取全部卖出时候的现金的投入
     * @param coinName -
     * @return -
     */
    private TBuyRecord getSaleAllInfo(String coinName){
        LambdaQueryWrapper<TBuyRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TBuyRecord::getCoinName,coinName);
        queryWrapper.eq(TBuyRecord::getType,CoinTypeEnum.SALE.getType());
        queryWrapper.orderByDesc(TBuyRecord::getCreateTime);
        queryWrapper.last(" limit 1");
        TBuyRecord one = buyRecordService.getOne(queryWrapper);
        if(null == one){
            one = new TBuyRecord();
            one.setPrincipal(new BigDecimal("0"));
            one.setCash(new BigDecimal("0"));
        }
        return one;
    }

}
