package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.Comment;
import com.sustech.campus.entity.CommentPhoto;
import com.sustech.campus.enums.CommentStatus;
import com.sustech.campus.mapper.BuildingMapper;
import com.sustech.campus.mapper.CommentMapper;
import com.sustech.campus.mapper.CommentPhotoMapper;
import com.sustech.campus.mapper.UserMapper;
import com.sustech.campus.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private UserMapper userMapper;

    private BuildingMapper buildingMapper;

    private CommentMapper commentMapper;

    private CommentPhotoMapper commentPhotoMapper;

    public CommentServiceImpl(UserMapper userMapper, BuildingMapper buildingMapper,
                              CommentMapper commentMapper, CommentPhotoMapper commentPhotoMapper) {
        this.userMapper = userMapper;
        this.buildingMapper = buildingMapper;
        this.commentMapper = commentMapper;
        this.commentPhotoMapper = commentPhotoMapper;
    }

    @Override
    public Comment addComment(Long buildingId, Long commenterId, String content) {
        if (this.buildingMapper.selectById(buildingId) == null || this.userMapper.selectById(commenterId) == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setBuildingId(buildingId);
        comment.setCommenterId(commenterId);
        comment.setContent(content);
        comment.setStatus(CommentStatus.REJECTED);
        comment.setTime(System.currentTimeMillis());
        this.commentMapper.insert(comment);
        return comment;
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return this.commentMapper.selectById(commentId);
    }

    @Override
    public boolean deleteComment(Long commentId) {
        if (!this.commentExists(commentId)) {
            return false;
        }
        QueryWrapper<CommentPhoto> wrapper = new QueryWrapper<>();
        wrapper.eq("commentId", commentId);
        this.commentPhotoMapper.delete(wrapper);
        this.commentMapper.deleteById(commentId);
        return true;
    }

    @Override
    public boolean rejectComment(Long commentId) {
        Comment comment = this.getCommentById(commentId);
        if (comment == null || comment.getStatus() != CommentStatus.APPROVED) {
            return false;
        }
        comment.setStatus(CommentStatus.REJECTED);
        this.commentMapper.updateById(comment);
        return true;
    }

    @Override
    public boolean approveComment(Long commentId) {
        Comment comment = this.getCommentById(commentId);
        if (comment == null || comment.getStatus() != CommentStatus.REJECTED) {
            return false;
        }
        comment.setStatus(CommentStatus.APPROVED);
        this.commentMapper.updateById(comment);
        return true;
    }

    @Override
    public boolean commentExists(Long commentId) {
        return this.getCommentById(commentId) != null;
    }

    @Override
    public CommentStatus getCommentStatus(Long commentId) {
        Comment comment = this.getCommentById(commentId);
        if (comment == null) {
            return null;
        }
        return comment.getStatus();
    }

    @Override
    public List<Comment> getComments(Long buildingId, Long commenterId, CommentStatus status) {
        QueryWrapper<Comment> wrapper;
        if (buildingId == null && commenterId == null && status == null) {
            wrapper = null;
        } else {
            wrapper = new QueryWrapper<>();
            if (buildingId != null) {
                wrapper.eq("buildingId", buildingId);
            }
            if (commenterId != null) {
                wrapper.eq("commenterId", commenterId);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
        }
        return this.commentMapper.selectList(wrapper);
    }

    @Override
    public List<CommentPhoto> getCommentPhotos(Long commentId) {
        if (!this.commentExists(commentId)) {
            return null;
        }
        QueryWrapper<CommentPhoto> wrapper = new QueryWrapper<>();
        wrapper.eq("commentId", commentId);
        return this.commentPhotoMapper.selectList(wrapper);
    }

    @Override
    public CommentPhoto getCommentPhotoById(Long photoId) {
        return this.commentPhotoMapper.selectById(photoId);
    }

    @Override
    public boolean commentPhotoExists(Long photoId) {
        return this.getCommentPhotoById(photoId) != null;
    }

    @Override
    public Long addCommentPhoto(Long commandId, String photoPath) {
        if (!this.commentExists(commandId)) {
            return null;
        }
        CommentPhoto photo = new CommentPhoto();
        photo.setCommentId(commandId);
        photo.setPath(photoPath);
        this.commentPhotoMapper.insert(photo);
        return photo.getId();
    }

    @Override
    public boolean deleteCommentPhoto(Long photoId) {
        if (!this.commentPhotoExists(photoId)) {
            return false;
        }
        this.commentPhotoMapper.deleteById(photoId);
        return true;
    }
}
