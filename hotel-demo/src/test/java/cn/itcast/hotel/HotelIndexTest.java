package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.service.impl.HotelService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static cn.itcast.hotel.constant.hotelConstant.MAPPING_TEMPLATE;

/**
 * @Author MisakiMikoto
 * @Date 2023/4/30 16:53
 */
@SpringBootTest
public class HotelIndexTest {
    private RestHighLevelClient client;



    @Test
    void testInit(){
        System.out.println(client);
    }

    @Test
    void createHotelIndex() throws IOException {
        // 1 创建 createIndexRequest对象
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        // 2 请求参数
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        // 3 发起请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }


    @Test
    void testDeleteHotelIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");

        client.indices().delete(request,RequestOptions.DEFAULT);
    }

    @Test
    void testExistHotelIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("hotel");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);

    }



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
}
