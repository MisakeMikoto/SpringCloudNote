package cn.itcast.mq.helloworld;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

/**
 * @Author MisakiMikoto
 * @Date 2023/4/23 17:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class test {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage2SimpleQueue() {
        String queueName = "simple.queue";
        String message = "Hello Spring Amqp!!";
        rabbitTemplate.convertAndSend(queueName, message);
    }


    @Test
    public void sendMessage2SimpleQueueByWorkQueue() throws InterruptedException {
        String queueName = "simple.queue";
        for (int i = 1; i <= 50; i++) {
            String message = "Hello Spring Amqp!!" + i;
            rabbitTemplate.convertAndSend(queueName, message);
            Thread.sleep(20);
        }
    }

    @Test
    public void sendMessage2SimpleQueueByFanoutQueue(){
        String exchangeName = "itcast.fanout";

        String message = "hello , everyone";

        rabbitTemplate.convertAndSend(exchangeName,null,message);
    }


    @Test
    public void sendMessage2SimpleQueueByDirectQueue(){
        String exchangeName = "itcast.direct";

        String message = "hello , blue";

        rabbitTemplate.convertAndSend(exchangeName,"blue",message);
    }

    @Test
    public void sendMessage2SimpleQueueByTopicQueue(){
        String exchangeName = "itcast.topic";

        String message = "china has a great weather";

        rabbitTemplate.convertAndSend(exchangeName,"china.weather",message);
    }


    @Test
    public void sendObjectMessage(){
        String queueName = "object.queue";

        HashMap<String, String> msg = new HashMap<>();
        msg.put("常盘台大小姐","御坂美琴");
        msg.put("原初的公主","爱尔奎特");

        rabbitTemplate.convertAndSend(queueName,msg);
    }
}
