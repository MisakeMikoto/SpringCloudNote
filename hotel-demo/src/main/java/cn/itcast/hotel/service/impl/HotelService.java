package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParam;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RankFeatureQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
    @Autowired
    private HotelMapper hotelMapper;

    @Autowired
    private RestHighLevelClient client;

    @Override
    public List<Hotel> getAll() {
        List<Hotel> hotels = hotelMapper.selectList(null);
        return hotels;
    }

    @Override
    public PageResult getlist(RequestParam param) {

        try {
            SearchRequest request = new SearchRequest("hotel");

            BoolQueryBuilder boolQuery = buildBasicQuery(param);


            int page = param.getPage();

            int size = param.getSize();

            FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                    //原始查询条件，相关性算分的查询
                    boolQuery,
                    //function score的数组
                    new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                            //其中一个 function score元素
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    // 加分条件
                                    QueryBuilders.termQuery("isAD", true),
                                    ScoreFunctionBuilders.weightFactorFunction(10)
                            )});

            request.source().query(functionScoreQuery);

            request.source().from((page - 1) * size).size(size);

            if(param.getLocation() != null && !param.getLocation().equals("")){

                request.source().sort(SortBuilders.geoDistanceSort("location",new GeoPoint(param.getLocation()))

                        .order(SortOrder.ASC)

                        .unit(DistanceUnit.KILOMETERS));
            }

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            return HandleResponse(response);


        } catch (IOException e) {
           throw new RuntimeException();
        }
    }

    @Override
    public Map<String, List<String>> getAggregation(RequestParam param) throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        BoolQueryBuilder boolQuery = buildBasicQuery(param);

        request.source().query(boolQuery);

        if(param.getLocation() != null && !param.getLocation().equals("")){

            request.source().sort(SortBuilders.geoDistanceSort("location",new GeoPoint(param.getLocation()))

                    .order(SortOrder.ASC)

                    .unit(DistanceUnit.KILOMETERS));
        }

        buildAggregations(request);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        Aggregations aggregations = response.getAggregations();

        HashMap<String, List<String>> map = new HashMap<>();

        ArrayList<String> brandList = getList(aggregations,"brandAgg");
        ArrayList<String> cityList = getList(aggregations,"cityAgg");
        ArrayList<String> starList = getList(aggregations,"starAgg");

        map.put("品牌",brandList);
        map.put("城市",cityList);
        map.put("星级",starList);


        return map;
    }

    private ArrayList<String> getList(Aggregations aggregations , String aggName) {
        Terms brandAggTerms = aggregations.get(aggName);

        ArrayList<String> brandList = new ArrayList<>();
        List<? extends Terms.Bucket> brandBuckets = brandAggTerms.getBuckets();

        for (Terms.Bucket brandBucket : brandBuckets) {
            String keyAsString = brandBucket.getKeyAsString();
            brandList.add(keyAsString);
        }
        return brandList;
    }

    private void buildAggregations(SearchRequest request) {
        request.source().aggregation(AggregationBuilders.terms("brandAgg").field("brand").size(100));
        request.source().aggregation(AggregationBuilders.terms("cityAgg").field("city").size(100));
        request.source().aggregation(AggregationBuilders.terms("starAgg").field("starName").size(100));
    }


    private BoolQueryBuilder buildBasicQuery(RequestParam param) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


        if(param.getCity() != null && !"".equals(param.getCity())){
            boolQuery.filter(QueryBuilders.termQuery("city",param.getCity()));
        }

        if(param.getBrand() != null && !"".equals(param.getBrand())){
            boolQuery.filter(QueryBuilders.termQuery("brand",param.getBrand()));
        }

        if(param.getStarName() != null && !"".equals(param.getStarName())){
            boolQuery.filter(QueryBuilders.termQuery("starName",param.getStarName()));
        }

        if(param.getMinPrice() != null && param.getMinPrice() != null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(param.getMinPrice()).lte(param.getMaxPrice()));
        }

        if(param.getKey() == null || "".equals(param.getKey())){
            boolQuery.must(QueryBuilders.matchAllQuery());
        }else{

            boolQuery.must(QueryBuilders.matchQuery("all",param.getKey()));
        }




        return boolQuery;
    }


    private PageResult HandleResponse(SearchResponse response) throws IOException {

        SearchHits hits = response.getHits();

        long value = hits.getTotalHits().value;

        SearchHit[] hits1 = hits.getHits();
        ArrayList<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits1) {
            String js = hit.getSourceAsString();

            HotelDoc hotelDoc = JSON.parseObject(js, HotelDoc.class);

            Object[] sortValues = hit.getSortValues();
            if(sortValues.length > 0){
                hotelDoc.setDistance(sortValues[0]);
            }

            hotels.add(hotelDoc);
        }
        return new PageResult(value, hotels);
    }
}
