package com.fwd.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSenderDemo {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 默认交换机
     * @param msg
     */
    public void send(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("queue", msg);
    }

    /**
     * 􏰡􏰃􏰄􏰐􏰑􏰇Fanout模式
     * @param msg
     */
    public void send02(Object msg) {
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }

    /**
     * Direct模式
     * @param msg
     */
    public void send03(Object msg) {
        log.info("发送red消息:"+msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
    }

    /**
     * Direct模式
     * @param msg
     */
    public void send04(Object msg) {
        log.info("发送green消息:"+msg);
        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
    }

    /**
     * Topic模式
     * @param msg
     */
    public void send05(Object msg) {
        log.info("发送消息(被01队列接受): " + msg);
        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
    }

    /**
     * Topic模式
     * @param msg
     */
    public void send06(Object msg) {
        log.info("发送消息(被两个queue接受): " + msg);
        rabbitTemplate.convertAndSend("topicExchange", "message.queue.green.abc", msg);
    }

    /**
     * headers模式
     * @param msg
     */
    public void send07(String msg) {
        log.info("发送消息(被两个queue接受): " + msg);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("color", "red");
        messageProperties.setHeader("speed", "fast");
        Message message = new Message(msg.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("headersExchange", "", message);
    }

    /**
     * headers模式
     * @param msg
     */
    public void send08(String msg) {
        log.info("发送消息(被01队列接受):" + msg);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("color", "red");
        messageProperties.setHeader("speed", "normal");
        Message message = new Message(msg.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("headersExchange", "", message);
    }
}
