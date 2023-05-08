package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

/**
 * @Author MisakiMikoto
 * @Date 2023/5/1 11:07
 */
@SpringBootTest
public class HotelBulkTest {
    @Autowired
    private IHotelService service;

    private RestHighLevelClient client;

    @BeforeEach
    void setUp(){
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.81.131:9200")
        ));
    }

    @AfterEach
    void setDown() throws IOException {
        client.close();
    }

    @Test
    void testBulkAddDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        List<Hotel> all = service.getAll();
        for (Hotel hotel : all) {
            bulkRequest.add(new IndexRequest("hotel").id(hotel.getId().toString()).source(
                    JSON.toJSONString(new HotelDoc(hotel)), XContentType.JSON
            ));
        }

        client.bulk(bulkRequest,RequestOptions.DEFAULT);

    }
}
