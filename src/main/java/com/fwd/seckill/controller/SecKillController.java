package com.fwd.seckill.controller;

import com.fwd.seckill.pojo.Order;
import com.fwd.seckill.pojo.SeckillOrder;
import com.fwd.seckill.pojo.User;
import com.fwd.seckill.rabbitmq.MQSender;
import com.fwd.seckill.rabbitmq.MQSenderDemo;
import com.fwd.seckill.rabbitmq.SecKillMessage;
import com.fwd.seckill.service.IGoodsService;
import com.fwd.seckill.service.IOrderService;
import com.fwd.seckill.service.ISeckillOrderService;
import com.fwd.seckill.utils.JsonUtil;
import com.fwd.seckill.vo.GoodsVO;
import com.fwd.seckill.vo.RespBean;
import com.fwd.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();  // 用于内存标记

    /**
     * 秒杀，优化前，QPS: 554.8/sec
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
//    @RequestMapping(value = "/doSeckill01", method = RequestMethod.POST)
    public String doSecKill01(Model model, User user, Long goodsId) {
        if (user == null)
            return "login";
        model.addAttribute("user", user);
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        // 判断库存
        if (goodsVO.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        // 判断是否重复抢购
        /*SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().
                eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }*/
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }
        Order order = orderService.seckill(user, goodsVO);
        if (order == null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }
        model.addAttribute("order", order);
        model.addAttribute("goods", goodsVO);

        return "orderDetail";
    }

    /**
     * 秒杀，页面静态化，QPS：1343.5/sec
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill02", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill02(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 判断是否重复抢购
        /*SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId()).eq( "goods_id", goodsId));*/

        // 通过Redis判断是否重复抢购
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String seckillOrderJson = (String) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        Order order = orderService.seckill(user, goods);
        if (order == null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        return RespBean.success(order);
    }

    /**
     * 秒杀，接口优化
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill03(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //判断是否重复抢购
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String seckillOrderJson = (String) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记,减少Redis访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存，原子性操作
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId);  // 让库存恢复为0
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        System.out.println("111111111111.stock = " + stock);
        // 请求入队，立即返回排队中（异步处理）
        SecKillMessage message = new SecKillMessage(user, goodsId);
        mqSender.sendsecKillMessage(JsonUtil.object2JsonStr(message));
        return RespBean.success(0);
    }

    /**
     * 系统初始化，把商品库存数量加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVO> list = goodsService.findGoodsVO();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),
                    goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }

    /**
     * 获取秒杀结果
     *
     * @param user
     * @param goodsId
     * @return orderId:成功，-1:秒杀失败，0:排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

}
