package com.fwd.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fwd.seckill.pojo.Goods;
import com.fwd.seckill.vo.GoodsVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fwd
 * @since 2022-04-04
 */
public interface IGoodsService extends IService<Goods> {

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
    GoodsVO findGoodsVOByGoodsId(long goodsId);

}
