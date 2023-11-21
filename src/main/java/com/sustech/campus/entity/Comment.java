package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sustech.campus.enums.CommentStatus;

@TableName("comment")
public class Comment {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String building, commenter, content;

    private CommentStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
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
                ", building='" + building + "'" +
                ", commenter='" + commenter + "'" +
                ", content='" + content + "'" +
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
                && this.building.equals(other.building)
                && this.commenter.equals(other.commenter)
                && this.content.equals(other.content)
                && this.status == other.status;
    }
}
