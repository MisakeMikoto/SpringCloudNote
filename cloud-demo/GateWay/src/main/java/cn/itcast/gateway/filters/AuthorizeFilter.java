package cn.itcast.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;


/**
 * @Author MisakiMikoto
 * @Date 2023/4/18 17:16
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Order {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        MultiValueMap<String, String> queryParams = request.getQueryParams();

        String auth = queryParams.getFirst("Authorization");

        if("MisakiMikoto".equals(auth)){
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    @Override
    public int value() {
        return -1;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
