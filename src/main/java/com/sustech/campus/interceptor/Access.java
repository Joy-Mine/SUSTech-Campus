package com.sustech.campus.interceptor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented     // 在生成javac时显示该注解的信息
@Inherited
public @interface Access {
    AccessLevel level() default AccessLevel.USER; //默认为ALL
}
