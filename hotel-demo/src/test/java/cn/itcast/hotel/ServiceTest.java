package cn.itcast.hotel;

import cn.itcast.hotel.service.IHotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author MisakiMikoto
 * @Date 2023/5/8 15:05
 */
@SpringBootTest
public class ServiceTest {

    @Autowired
    private IHotelService service;


//    @Test
//    void getAggregationsTest() throws IOException {
//        Map<String, List<String>> aggregation = service.getAggregation();
//
//        for (Map.Entry<String, List<String>> entry : aggregation.entrySet()) {
//            String key = entry.getKey();
//
//            List<String> value = entry.getValue();
//            System.out.print(key + ": [ ");
//
//            for (String s : value) {
//                System.out.print(s+" ");
//            }
//            System.out.println("]");
//        }
//    }
}


