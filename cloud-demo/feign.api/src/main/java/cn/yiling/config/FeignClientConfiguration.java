package cn.yiling.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @Author MisakiMikoto
 * @Date 2023/4/18 15:20
 */
public class FeignClientConfiguration {
    @Bean
    public Logger.Level feignLogLevel(){
        return Logger.Level.BASIC;
    }
}
