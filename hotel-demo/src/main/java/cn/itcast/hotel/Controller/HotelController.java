package cn.itcast.hotel.Controller;

import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParam;
import cn.itcast.hotel.service.IHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author MisakiMikoto
 * @Date 2023/5/2 11:03
 */
@RestController
@RequestMapping("hotel")
public class HotelController {

    @Autowired
    private IHotelService service;

    @PostMapping("list")
    public PageResult getList(@RequestBody RequestParam requestParam){
        return service.getlist(requestParam);
    }

    @PostMapping("filters")
    public Map<String, List<String>> getFilters(@RequestBody RequestParam param){
        try {
            Map<String, List<String>> aggregation = service.getAggregation(param);
            return aggregation;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
