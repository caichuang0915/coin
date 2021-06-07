package com.deal.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.deal.coin.entity.TBuyRecord;
import com.deal.coin.vo.BuyListReqVo;

import java.util.List;

public interface ITBuyRecordService extends IService<TBuyRecord> {

    /**
     * 查询买入的记录
     * @param reqVo -
     * @return -
     */
    TBuyRecord getRecentBuy(BuyListReqVo reqVo);

}
