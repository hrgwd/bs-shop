package com.example.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

@Configuration
public class WebConfig implements  WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(Objects.requireNonNull(jwtInterceptor)).addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register")
                .excludePathPatterns("/files/**")
                .excludePathPatterns("/type/**")
                .excludePathPatterns("/notice/selectAll")
                .excludePathPatterns("/goods/**")
                .excludePathPatterns("/comment/selectByGoodsId/**")
                .excludePathPatterns("/chat/**")
                .excludePathPatterns("/alipay/pay")
                .excludePathPatterns("/alipay/notify")
                .excludePathPatterns("/alipay/return");
        
    }
}