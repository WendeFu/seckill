package com.fwd.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfigDemo {

    /************ 默认交换机 *************/

    @Bean
    public Queue queue00() {
        return new Queue("queue", true);
    }

    /************** Fanout模式（广播模式） *************/

    private static final String QUEUE_FANOUT01 = "queue_fanout01";
    private static final String QUEUE_FANOUT02 = "queue_fanout02";
    private static final String FANOUT_EXCHANGE = "fanoutExchange";

    @Bean
    public Queue queue01() {
        return new Queue(QUEUE_FANOUT01);
    }

    @Bean
    public Queue queue02() {
        return new Queue(QUEUE_FANOUT02);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding binding01() {
        return BindingBuilder.bind(queue01()).to(fanoutExchange());
    }

    @Bean
    public Binding binding02() {
        return BindingBuilder.bind(queue02()).to(fanoutExchange());
    }

    /************** Direct模式 *************/

    private static final String QUEUE_DIRECT01 = "queue_direct01";
    private static final String QUEUE_DIRECT02 = "queue_direct02";
    private static final String DIRECT_EXCHANGE = "directExchange";
    private static final String ROUTING_KEY01 = "queue.red";
    private static final String ROUTING_KEY02 = "queue.green";

    @Bean
    public Queue queue03() {
        return new Queue(QUEUE_DIRECT01);
    }

    @Bean
    public Queue queue04() {
        return new Queue(QUEUE_DIRECT02);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Binding binding03() {
        return BindingBuilder.bind(queue03()).to(directExchange()).with(ROUTING_KEY01);
    }

    @Bean
    public Binding binding04() {
        return BindingBuilder.bind(queue04()).to(directExchange()).with(ROUTING_KEY02);
    }

    /************** Topic模式 *************/
    private static final String QUEUE_TOPIC01 = "queue_topic01";
    private static final String QUEUE_TOPIC02 = "queue_topic02";
    private static final String TOPIC_EXCHANGE = "topicExchange";
    private static final String ROUTING_KEY03 = "#.queue.#";
    private static final String ROUTING_KEY04 = "*.queue.#";

    @Bean
    public Queue queue05() {
        return new Queue(QUEUE_TOPIC01);
    }

    @Bean
    public Queue queue06() {
        return new Queue(QUEUE_TOPIC02);
    }

    @Bean
    public TopicExchange topicExchange01() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Binding binding05() {
        return BindingBuilder.bind(queue05()).to(topicExchange01()).with(ROUTING_KEY03);
    }

    @Bean
    public Binding binding06() {
        return BindingBuilder.bind(queue06()).to(topicExchange01()).with(ROUTING_KEY04);
    }

    /************** Headers模式 *************/
    private static final String QUEUE_HEADER01 = "queue_headers01";
    private static final String QUEUE_HEADER02 = "queue_headers02";
    private static final String HEADERS_EXCHANGE = "headersExchange";

    @Bean
    public Queue queue07() {
        return new Queue(QUEUE_HEADER01);
    }

    @Bean
    public Queue queue08() {
        return new Queue(QUEUE_HEADER02);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Binding binding07() {
        Map<String, Object> map = new HashMap();
        map.put("color", "red");
        map.put("speed", "low");
        return BindingBuilder.bind(queue07()).to(headersExchange()).whereAny(map).match();
    }

    @Bean
    public Binding binding08() {
        Map<String, Object> map = new HashMap<>();
        map.put("color", "red");
        map.put("speed", "fast");
        return BindingBuilder.bind(queue08()).to(headersExchange()).whereAll(map).match();
    }

}
