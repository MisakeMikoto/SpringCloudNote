package cn.itcast.hotel.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author MisakiMikoto
 * @Date 2023/5/2 11:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult {

    private Long total;

    private List<HotelDoc> hotels;
}
