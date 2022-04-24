package com.fwd.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fwd.seckill.exception.GlobalException;
import com.fwd.seckill.mapper.OrderMapper;
import com.fwd.seckill.pojo.Order;
import com.fwd.seckill.pojo.SeckillGoods;
import com.fwd.seckill.pojo.SeckillOrder;
import com.fwd.seckill.pojo.User;
import com.fwd.seckill.service.IGoodsService;
import com.fwd.seckill.service.IOrderService;
import com.fwd.seckill.service.ISeckillGoodsService;
import com.fwd.seckill.service.ISeckillOrderService;
import com.fwd.seckill.utils.JsonUtil;
import com.fwd.seckill.vo.GoodsVO;
import com.fwd.seckill.vo.OrderDetailVO;
import com.fwd.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fwd
 * @since 2022-04-04
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVO goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().
                eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        //seckillGoodsService.updateById(seckillGoods);
        // 防止库存超卖
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = " + "stock_count - 1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0));
        /*if (!seckillGoodsResult) {
            return null;
        }*/
        if (seckillGoods.getStockCount() < 0) {
            //判断是否还有库存
            valueOperations.set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        //System.out.println("2222222222.seckillGoods.getStockCount() = " + seckillGoods.getStockCount());
        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        // 将秒杀订单信息存入Redis，方便判断是否重复抢购时进行查询
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" +
                        goods.getId(), JsonUtil.object2JsonStr(seckillOrder));
        return order;
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVO detail(Long orderId) {
        if (null == orderId) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVO goodsVo = goodsService.findGoodsVOByGoodsId(order.getGoodsId());
        OrderDetailVO detail = new OrderDetailVO();
        detail.setGoodsVO(goodsVo);
        detail.setOrder(order);
        return detail;
    }
}
