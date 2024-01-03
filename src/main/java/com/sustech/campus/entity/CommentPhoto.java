package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("comment_photo")
public class CommentPhoto {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "commentId")
    private Long commentId;

    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "CommentPhoto{" +
                "id=" + id +
                ", commentId=" + commentId +
                ", path='" + path + "'" +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CommentPhoto other)) {
            return false;
        }
        return this == other
                || this.id.equals(other.id)
                && this.commentId.equals(other.commentId)
                && this.path.equals(other.path);
    }
}
