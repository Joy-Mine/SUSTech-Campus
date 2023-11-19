package com.sustech.campus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sustech.campus.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
