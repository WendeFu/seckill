package com.fwd.seckill.controller;


import com.fwd.seckill.pojo.User;
import com.fwd.seckill.service.IOrderService;
import com.fwd.seckill.vo.OrderDetailVO;
import com.fwd.seckill.vo.RespBean;
import com.fwd.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fwd
 * @since 2022-04-04
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if (null==user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVO detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }

}
