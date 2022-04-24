package com.fwd.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiverDemo {

    /**
     * 默认交换机
     * @param msg
     */
    @RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接受消息：" + msg);
    }

    /**
     * Fanout模式
     * @param msg
     */
    @RabbitListener(queues = "queue_fanout01")
    public void receive02(Object msg) {
        log.info("QUEUE01接受消息：" + msg);
    }

    /**
     * Fanout模式
     * @param msg
     */
    @RabbitListener(queues = "queue_fanout02")
    public void receive03(Object msg) {
        log.info("QUEUE02接受消息：" + msg);
    }

    /**
     * Direct模式
     * @param msg
     */
    @RabbitListener(queues = "queue_direct01")
    public void receive04(Object msg) {
        log.info("QUEUE01接受消息:" + msg);
    }

    /**
     * Direct模式
     * @param msg
     */
    @RabbitListener(queues = "queue_direct02")
    public void receive05(Object msg) {
        log.info("QUEUE02接受消息：" + msg);
    }

    /**
     * Topic模式
     * @param msg
     */
    @RabbitListener(queues = "queue_topic01")
    public void receive06(Object msg) {
        log.info("QUEUE01接受消息:" + msg);
    }

    /**
     * Topic模式
     * @param msg
     */
    @RabbitListener(queues = "queue_topic02")
    public void receive07(Object msg) {
        log.info("QUEUE02接受消息:" + msg);
    }

    /**
     * Headers模式
     * @param message
     */
    @RabbitListener(queues = "queue_headers01")
    public void receive08(Message message) {
        log.info("QUEUE01接受Message对象:" + message);
        log.info("QUEUE01接受消息:" + new String(message.getBody()));
    }

    /**
     * Headers模式
     * @param message
     */
    @RabbitListener(queues = "queue_headers02")
    public void receive09(Message message) {
        log.info("QUEUE02接受Message对象：" + message);
        log.info("QUEUE02接受消息：" + new String(message.getBody()));
    }
}
