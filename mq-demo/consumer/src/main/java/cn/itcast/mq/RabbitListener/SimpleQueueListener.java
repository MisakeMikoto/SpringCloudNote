package cn.itcast.mq.RabbitListener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @Author MisakiMikoto
 * @Date 2023/4/23 19:21
 */
@Component
public class SimpleQueueListener {

//    @RabbitListener(queues = {"simple.queue"})
//    public void listener(String msg){
//        System.out.println("消费者收到消息"+"{"+msg+"}");
//    }


//    @RabbitListener(queues = {"simple.queue"})
//    public void listenerByWorkQueue1(String msg) throws InterruptedException {
//        System.out.println("消费者1收到消息"+"{"+msg+"}"+ LocalDateTime.now());
//        Thread.sleep(20);
//    }
//
//
//    @RabbitListener(queues = {"simple.queue"})
//    public void listenerByWorkQueue2(String msg) throws InterruptedException {
//        System.err.println("消费者2收到消息"+"{"+msg+"}"+LocalDateTime.now());
//        Thread.sleep(200);
//    }


    @RabbitListener(queues = {"fanout.queue1"})
    public void listenerByFanoutQueue1(String msg) throws InterruptedException {
        System.err.println("消费者1收到消息"+"{"+msg+"}"+LocalDateTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(queues = {"fanout.queue2"})
    public void listenerByFanoutQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2收到消息"+"{"+msg+"}"+LocalDateTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "itcast.direct"),
            key = {"red","blue"}
    ))
    public void listenerByDirectQueue1(String msg) throws InterruptedException {
        System.err.println("消费者1收到消息"+"{"+msg+"}"+LocalDateTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "itcast.direct"),
            key = {"red","yellow"}
    ))
    public void listenerByDirectQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2收到消息"+"{"+msg+"}"+LocalDateTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "itcast.topic",type = ExchangeTypes.TOPIC),
            key = "china.#"
    ))
    public void listenerByTopicQueue1(String msg) throws InterruptedException {
        System.err.println("消费者1收到Topic消息"+"{"+msg+"}"+LocalDateTime.now());
        Thread.sleep(200);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue2"),
            exchange = @Exchange(name = "itcast.topic",type = ExchangeTypes.TOPIC),
            key = "#.news"
    ))
    public void listenerByTopicQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2收到Topic消息"+"{"+msg+"}"+LocalDateTime.now());
        Thread.sleep(200);
    }


    @RabbitListener(queues = "object.queue")
    public void objectQueueListener(HashMap msg){
        System.out.println("收到来自object.queue的消息"+msg);
    }


}
