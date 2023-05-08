package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author MisakiMikoto
 * @Date 2023/5/1 17:52
 */
@SpringBootTest
public class HotelSearchTest {
    private RestHighLevelClient client;

    @BeforeEach
    void setUp(){
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.81.131:9200")
        ));
    }

    @AfterEach
    void  setDown() throws IOException {
        client.close();
    }


    @Test
    void matchAllTest() throws IOException {

        SearchRequest request = new SearchRequest("hotel");

        request.source()
                .query(QueryBuilders.matchAllQuery());

        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        HandleResponse(response);
    }


    @Test
    void matchTest() throws IOException {

        SearchRequest request = new SearchRequest("hotel");

        request.source()
                .query(QueryBuilders.matchQuery("all","如家"));

        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        HandleResponse(response);
    }

    @Test
    void boolTest() throws IOException {

        SearchRequest request = new SearchRequest("hotel");

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.must(QueryBuilders.termQuery("city","上海"));

        boolQuery.filter(QueryBuilders.rangeQuery("price").gte(250));

        request.source().query(boolQuery);

        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        HandleResponse(response);
    }

    @Test
    void SortAndPageTest() throws IOException {
        int page = 1, size = 20;

        SearchRequest request = new SearchRequest("hotel");

        request.source().query(QueryBuilders.matchAllQuery());

        request.source().sort("price", SortOrder.ASC);

        request.source().from((page - 1)*size).size(size);

        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        HandleResponse(response);
    }

    @Test
    void HighLightTest() throws IOException {
        int page = 1, size = 20;

        SearchRequest request = new SearchRequest("hotel");

        request.source().query(QueryBuilders.matchQuery("all","如家"));

        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));

        SearchResponse response = client.search(request,RequestOptions.DEFAULT);

        HandleResponse(response);
    }


    @Test
    void testAggsBucket() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source().size(0);

        request.source().aggregation(AggregationBuilders.terms("brandAggs").field("brand").size(20));

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

//        System.out.println(response);

        Aggregations aggregations = response.getAggregations();

        Terms brandAggs = aggregations.get("brandAggs");

        List<? extends Terms.Bucket> buckets = brandAggs.getBuckets();

        for (Terms.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            System.out.println(keyAsString + " count=" + docCount);
        }

    }


    @Test
    void testAggsMetrics() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source().size(0);

        request.source().aggregation(AggregationBuilders
                .terms("brandAgg")
                .field("brand")
                .size(20)
                .order(BucketOrder.aggregation("scoreAgg.avg",true))
                .subAggregation(AggregationBuilders.stats("scoreAgg").field("score")));


        SearchResponse response = client.search(request, RequestOptions.DEFAULT);


//        System.out.println(response);

        Aggregations aggregations = response.getAggregations();

        Terms brandAggs = aggregations.get("brandAgg");

        List<? extends Terms.Bucket> buckets = brandAggs.getBuckets();

        for (Terms.Bucket bucket : buckets) {
            Aggregations aggregations1 = bucket.getAggregations();
            Stats scoreAgg = aggregations1.get("scoreAgg");
            double avg = scoreAgg.getAvg();
            System.out.println(avg);
        }

    }

    private void HandleResponse(SearchResponse response) throws IOException {

        SearchHits hits = response.getHits();

        long value = hits.getTotalHits().value;

        System.out.println("一共查询到了"+value+"条");

        SearchHit[] hits1 = hits.getHits();

        for (SearchHit hit : hits1) {
            String js = hit.getSourceAsString();

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HotelDoc hotelDoc = JSON.parseObject(js, HotelDoc.class);

            if(!CollectionUtils.isEmpty(highlightFields)){
                HighlightField field = highlightFields.get("name");
                String name = field.getFragments()[0].string();
                if(name != null){
                    hotelDoc.setName(name);
                }
            }
            System.out.println(hotelDoc);
        }
    }
}
