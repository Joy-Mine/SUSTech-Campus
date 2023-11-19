package com.sustech.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustech.campus.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
//    @Select("select * from users")
//    public List<User> find();
}
