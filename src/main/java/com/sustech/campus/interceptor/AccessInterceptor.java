package com.sustech.campus.interceptor;

import com.google.gson.Gson;
import com.sustech.campus.config.APIResponse;
import com.sustech.campus.config.ResponeCode;
import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public void setUserService( UserService userService) {
        AccessInterceptor.userService = userService;
    }
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
//            String token = request.getHeader("TOKEN");
//            if(userService.getUserByToken(token) == null) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401状态码
//                response.getWriter().write("Unauthorized");
//                return false;
//            }
            // 如果服务方法的注解为null，直接放过
            System.out.println("服务注解为null，无用户权限要求");
//            System.out.println(handlerMethod.toString()+"*****"+method.toString());
            return true;
        }

        // 管理员
        if(access.level() == UserType.ADMIN) {
            String token = request.getHeader("TOKEN");
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
        if(access.level() == UserType.USER) {
            String token = request.getHeader("TOKEN");
            User user = userService.getUserByToken(token);
//            if(user != null && user.getRole().equals(String.valueOf(User.NormalUser))){
            if(user != null && (user.getType()==UserType.USER || user.getType()==UserType.ADMIN)){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "未登录");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        System.out.println("*****");
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