package com.fwd.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fwd.seckill.pojo.Order;
import com.fwd.seckill.pojo.User;
import com.fwd.seckill.vo.GoodsVO;
import com.fwd.seckill.vo.OrderDetailVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fwd
 * @since 2022-04-04
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     * @param user
     * @param goodsVO
     * @return
     */
    Order seckill(User user, GoodsVO goodsVO);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVO detail(Long orderId);

}
