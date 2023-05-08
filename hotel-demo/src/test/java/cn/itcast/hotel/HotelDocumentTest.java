package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
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

/**
 * @Author MisakiMikoto
 * @Date 2023/5/1 10:35
 */
@SpringBootTest
public class HotelDocumentTest {
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
    void AddDocumentTest() throws IOException {
        Hotel hotel = service.getById(61083);

        HotelDoc hotelDoc = new HotelDoc(hotel);

        IndexRequest request = new IndexRequest("hotel").id(hotel.getId().toString());

        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);

        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void  GetDocumentTest() throws IOException {

        GetRequest request = new GetRequest("hotel").id("61083");

        GetResponse response = client.get(request, RequestOptions.DEFAULT);

        String source = response.getSourceAsString();

        HotelDoc hotel = JSON.parseObject(source, HotelDoc.class);

        System.out.println(hotel);
    }

    @Test
    void updateDocumentTest() throws IOException {

        UpdateRequest request = new UpdateRequest("hotel", "61083");

        request.doc(
                "price", "751",
                "starName", "四钻"
        );


        client.update(request, RequestOptions.DEFAULT);
    }

    @Test
    void deleteDocumentTest() throws IOException {
        DeleteRequest request = new DeleteRequest("hotel", "61083");

        client.delete(request, RequestOptions.DEFAULT);
    }

}
