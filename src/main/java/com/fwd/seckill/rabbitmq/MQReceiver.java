package com.fwd.seckill.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fwd.seckill.pojo.SeckillOrder;
import com.fwd.seckill.pojo.User;
import com.fwd.seckill.service.IGoodsService;
import com.fwd.seckill.service.IOrderService;
import com.fwd.seckill.utils.JsonUtil;
import com.fwd.seckill.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("QUEUE接受消息:" + msg);
        SecKillMessage message = JsonUtil.jsonStr2Object(msg, SecKillMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1) {
            return;
        }
        // 判断是否重复抢购
        String seckillOrderJson = (String) redisTemplate.opsForValue()
                .get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return;
        }
        orderService.seckill(user, goods);
    }
}
