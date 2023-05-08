package cn.itcast.order.service;


import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import cn.yiling.clients.UserClients;
import cn.yiling.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {


//    @Autowired
//    private RestTemplate restTemplate;
//
//    public Order queryOrderById(Long orderId) {
//        // 1.查询订单
//        Order order = orderMapper.findById(orderId);
//
//        String url = "http://UserService/user/"+order.getUserId();
//        User user = restTemplate.getForObject(url, User.class);
//
//        order.setUser(user);
//        // 4.返回
//        return order;
//    }

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserClients userClients;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);

        User user = userClients.findById(order.getUserId());

        order.setUser(user);
        // 4.返回
        return order;
    }
}
