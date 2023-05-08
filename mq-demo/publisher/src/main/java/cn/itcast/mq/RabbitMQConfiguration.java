package cn.itcast.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author MisakiMikoto
 * @Date 2023/4/23 20:42
 */
@Configuration
public class RabbitMQConfiguration {

    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("itcast.fanout");
    }


    @Bean
    public Queue fanoutQueue1(){
        return new Queue("fanout.queue1");
    }

    @Bean
    public Queue fanoutQueue2(){
        return new Queue("fanout.queue2");
    }

    @Bean
    public Binding binding1(FanoutExchange fanoutExchange, Queue fanoutQueue1){
        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    @Bean
    public Binding binding2(FanoutExchange fanoutExchange, Queue fanoutQueue2){
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("itcast.direct");
    }

    @Bean
    public Queue directQueue1(){
        return new Queue("direct.queue1");
    }

    @Bean
    public Queue directQueue2(){
        return new Queue("direct.queue2");
    }


    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("itcast.topic");
    }

    @Bean
    public Queue topicQueue1(){
        return new Queue("topic.queue1");
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue("topic.queue2");
    }

    @Bean
    public Queue objectQueue(){
        return new Queue("object.queue");
    }

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
