package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sustech.campus.enums.CommentStatus;

@TableName("comment")
public class Comment {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String content;

    @TableField(value = "commenterId")
    private Long commenterId;

    @TableField(value = "buildingId")
    private Long buildingId;

    private CommentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(Long commenterId) {
        this.commenterId = commenterId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", commenterId=" + commenterId +
                ", buildingId=" + buildingId +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Comment other)) {
            return false;
        }
        return this == other
                || this.id.equals(other.id)
                && this.buildingId.equals(other.buildingId)
                && this.commenterId.equals(other.commenterId)
                && this.content.equals(other.content)
                && this.status == other.status;
    }
}
