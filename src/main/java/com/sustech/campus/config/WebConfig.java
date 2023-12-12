package com.sustech.campus.config;

import com.sustech.campus.interceptor.AccessInterceptor;
import com.sustech.campus.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/user/login","/user/register");
        registry.addInterceptor(new AccessInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register");
    }
}
