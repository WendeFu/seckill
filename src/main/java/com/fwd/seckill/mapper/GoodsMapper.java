package com.fwd.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fwd.seckill.pojo.Goods;
import com.fwd.seckill.vo.GoodsVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author fwd
 * @since 2022-04-04
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVO> findGoodsVO();

    /**
     * 根据商品id获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVO findGoodsVOByGoodsId(Long goodsId);

}
