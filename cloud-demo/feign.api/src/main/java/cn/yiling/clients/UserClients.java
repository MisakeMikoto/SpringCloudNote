package cn.yiling.clients;

import cn.yiling.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author MisakiMikoto
 * @Date 2023/4/18 14:32
 */

@FeignClient("UserService")
public interface UserClients {

    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") Long id);

}
