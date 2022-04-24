package com.fwd.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fwd.seckill.mapper.GoodsMapper;
import com.fwd.seckill.pojo.Goods;
import com.fwd.seckill.service.IGoodsService;
import com.fwd.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fwd
 * @since 2022-04-04
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVO> findGoodsVO() {
        return goodsMapper.findGoodsVO();
    }

    @Override
    public GoodsVO findGoodsVOByGoodsId(long goodsId) {
        return goodsMapper.findGoodsVOByGoodsId(goodsId);
    }
}
