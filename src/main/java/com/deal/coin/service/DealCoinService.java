package com.deal.coin.service;

import com.deal.coin.entity.TBuyRecord;
import com.deal.coin.vo.CurBuyInfo;
import org.springframework.stereotype.Service;

/**
 * @Author Caichuang
 * @Date 2021/6/5 11:27 PM
 * @Description
 */
@Service
public interface DealCoinService {


    TBuyRecord getResentRecord(String coinName);

    void dealCoinPrice(String coinName,String price);

}
