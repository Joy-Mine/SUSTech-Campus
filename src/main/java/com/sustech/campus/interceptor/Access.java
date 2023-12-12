package com.sustech.campus.interceptor;

import com.sustech.campus.enums.UserType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Access {
    UserType level() default UserType.VISITOR; //默认为游客
}
