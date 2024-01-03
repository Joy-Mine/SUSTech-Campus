package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sustech.campus.enums.CommentStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String content;

    @TableField(value = "commenterId")
    private Long commenterId;

    @TableField(value = "buildingId")
    private Long buildingId;

    private CommentStatus status;

    private Long time;

    @TableField(exist = false)
    private List<CommentPhoto> photos;

    @TableField(exist = false)
    private String commenterName;

    @TableField(exist = false)
    private String buildingName;

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

    public List<CommentPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<CommentPhoto> photos) {
        this.photos = photos;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getStringTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(this.time));
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", commenterId=" + commenterId +
                ", buildingId=" + buildingId +
                ", status=" + status +
                ", time=" + time +
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
                && this.status == other.status
                && this.time.equals(other.time);
    }
}
