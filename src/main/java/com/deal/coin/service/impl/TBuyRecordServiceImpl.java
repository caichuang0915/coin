package com.deal.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deal.coin.vo.BuyListReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.deal.coin.entity.TBuyRecord;
import com.deal.coin.mapper.TBuyRecordMapper;
import com.deal.coin.service.ITBuyRecordService;

import java.util.List;

@Slf4j
@Service
public class TBuyRecordServiceImpl extends ServiceImpl<TBuyRecordMapper, TBuyRecord> implements ITBuyRecordService {


    @Override
    public TBuyRecord getRecentBuy(BuyListReqVo reqVo) {
        LambdaQueryWrapper<TBuyRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TBuyRecord::getCoinName,reqVo.getCoinName());
        if(null!=reqVo.getType()){
            queryWrapper.eq(TBuyRecord::getType,reqVo.getType());
        }
        if(null!=reqVo.getStatus()){
            queryWrapper.eq(TBuyRecord::getStatus,reqVo.getStatus());
        }
        queryWrapper.orderByDesc(TBuyRecord::getCreateTime);
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }
}
