package com.fwd.seckill.controller;


import com.fwd.seckill.pojo.User;
import com.fwd.seckill.rabbitmq.MQSenderDemo;
import com.fwd.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author fwd
 * @since 2022-03-28
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSenderDemo mqSenderDemo;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

    /**
     * 默认交换机
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSenderDemo.send("你好");
    }

    /**
     * Fanout模式
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq02() {
        mqSenderDemo.send02("你好啊");
    }

    /**
     * Direct模式
     */
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq03() {
        mqSenderDemo.send03("你好好啊");
    }

    /**
     * Direct模式
     */
    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq04() {
        mqSenderDemo.send04("你好好啊啊");
    }

    /**
     * Topic模式
     */
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void mq05() {
        mqSenderDemo.send05("hello, red");
    }

    /**
     * Topic模式
     */
    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void mq06() {
        mqSenderDemo.send06("hello, green");
    }

    /**
     * Headers模式
     */
    @RequestMapping("/mq/headers01")
    @ResponseBody
    public void mq07() {
        mqSenderDemo.send07("Hello,header01");
    }

    /**
     * Headers模式
     */
    @RequestMapping("/mq/headers02")
    @ResponseBody
    public void mq08() {
        mqSenderDemo.send08("Hello,header02");
    }
}
