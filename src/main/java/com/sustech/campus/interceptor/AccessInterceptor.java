package com.sustech.campus.interceptor;

import com.google.gson.Gson;
import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;


@Component
public class AccessInterceptor implements HandlerInterceptor {
    private static UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Access access = method.getAnnotation(Access.class);
        if (access == null) {
            // 如果注解为null, 不需要拦截, 直接放过
            return true;
        }

        // 管理员
        if(access.level().getCode() == AccessLevel.ADMIN.getCode()) {
            String token = request.getHeader("ADMINTOKEN");
            User user = userService.getUserByToken(token);
//            if(user != null && user.getRole().equals(String.valueOf(User.AdminUser))){
            if(user != null && user.getType()==UserType.ADMIN){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "无操作权限");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        // 用户
        if(access.level().getCode() == AccessLevel.USER.getCode()) {
            String token = request.getHeader("TOKEN");
            User user = userService.getUserByToken(token);
//            if(user != null && user.getRole().equals(String.valueOf(User.NormalUser))){
            if(user != null && user.getType()==UserType.USER){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "未登录");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        return true;
    }

    public void writeResponse(HttpServletResponse response, APIResponse apiResponse) throws IOException {
        response.setStatus(200);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(apiResponse);
        response.getWriter().println(jsonStr);
        response.getWriter().flush();
    }
}