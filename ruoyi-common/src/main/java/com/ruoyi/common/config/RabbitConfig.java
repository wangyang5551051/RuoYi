package com.ruoyi.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;


/**
 * Broker:它提供一种传输服务,它的角色就是维护一条从生产者到消费者的路线，保证数据能按照指定的方式进行传输, 
 * Exchange：消息交换机,它指定消息按什么规则,路由到哪个队列。 
 * Queue:消息的载体,每个消息都会被投到一个或多个队列。 
 * Binding:绑定，它的作用就是把exchange和queue按照路由规则绑定起来. 
 * Routing Key:路由关键字,exchange根据这个关键字进行消息投递。 
 * vhost:虚拟主机,一个broker里可以有多个vhost，用作不同用户的权限分离。 
 * Producer:消息生产者,就是投递消息的程序. 
 * Consumer:消息消费者,就是接受消息的程序. 
 * Channel:消息通道,在客户端的每个连接里,可建立多个channel.
 */
@Configuration
@PropertySource("classpath:/properties/application.properties")
public class RabbitConfig {

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    public static final String EXCHANGE_A = "my-mq-exchange_A";

    public static final String EXCHANGE_B = "my-mq-exchange_B";

    public static final String EXCHANGE_C = "my-mq-exchange_C";

    public static final String QUEUE_A = "QUEUE_A";

    public static final String QUEUE_B = "QUEUE_B";

    public static final String QUEUE_C = "QUEUE_C";

    public static final String ROUTINGKEY_A = "spring-boot-routingKey_A";

    public static final String ROUTINGKEY_B = "spring-boot-routingKey_B";

    public static final String ROUTINGKEY_C = "spring-boot-routingKey_C";


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(false);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }


    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
//    @Bean
//    public FanoutExchange defaultExchange() {
//        return new FanoutExchange(EXCHANGE_A);
//    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     HeadersExchange ：通过添加属性key-value匹配
     DirectExchange:按照routingkey分发到指定队列
     TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange defaultExchangeC() {
        //作为死信交换机
        return new DirectExchange(EXCHANGE_C);
    }

    /**
     * 获取队列A
     * @return
     */
    @Bean
    public Queue queueA() {
        //设置死信队列
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange",EXCHANGE_C);
        args.put("x-dead-letter-routing-key",ROUTINGKEY_C);
        return QueueBuilder.durable(QUEUE_A).withArguments(args).build();//队列持久
    }

    /**
     * 获取队列B
     * @return
     */
    @Bean
    public Queue queueB() {
        return new Queue(QUEUE_B, true);//队列持久
    }

    /**
     * 获取队列C
     * @return
     */
    @Bean
    public Queue queueC() {
        return new Queue(QUEUE_C, true);//队列持久
    }

    //一个交换机可以绑定多个消息队列，也就是消息通过一个交换机，可以分发到不同的队列当中去。

    /**
     * 绑定队列A到交换机A
     * @return
     */
//    @Bean
//    public Binding binding() {
//        return BindingBuilder.bind(queueA()).to(defaultExchange());
//    }



    /**
     * 绑定队列B到交换机A
     * @return
     */
    @Bean
    public Binding bindingB(){
        return BindingBuilder.bind(queueB()).to(defaultExchangeC()).with(ROUTINGKEY_B);
    }

    /**
     * 绑定队列C到死信交换机C 通过KEY
     * @return
     */
//    @Bean
//    public Binding bindingC(){
//        return BindingBuilder.bind(queueC()).to(defaultExchangeC()).with(ROUTINGKEY_C);
//    }

}