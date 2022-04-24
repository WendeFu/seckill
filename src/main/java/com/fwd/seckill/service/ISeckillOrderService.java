package com.fwd.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fwd.seckill.pojo.SeckillOrder;
import com.fwd.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fwd
 * @since 2022-04-04
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId * @return
     */
    Long getResult(User user, Long goodsId);

}
