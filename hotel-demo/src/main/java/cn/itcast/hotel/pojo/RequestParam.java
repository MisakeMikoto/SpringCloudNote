package cn.itcast.hotel.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author MisakiMikoto
 * @Date 2023/5/2 11:09
 */
@Data
@NoArgsConstructor
public class RequestParam {

    private String key;

    private Integer size;

    private Integer page;

    private String sortBy;

    private String brand;

    private String starName;

    private String city;

    private Integer minPrice;

    private Integer maxPrice;

    private String location;
}
