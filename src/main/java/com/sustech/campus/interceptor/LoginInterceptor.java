package com.sustech.campus.interceptor;

//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("登录/注册");
//        System.out.println(request.getAuthType());
//        System.out.println(request.getPart("name"));
//        System.out.println(request.getCookies().toString());
//        System.out.println(request.getHeaders("account"));
        System.out.println(response.toString());
        return true;
    }
}
