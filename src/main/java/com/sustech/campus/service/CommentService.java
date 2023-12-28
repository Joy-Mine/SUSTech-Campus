package com.sustech.campus.service;

import com.sustech.campus.entity.Comment;
import com.sustech.campus.entity.CommentPhoto;
import com.sustech.campus.enums.CommentStatus;

import java.util.List;

public interface CommentService {
    /**
     * @param buildingId
     * @param commenterId
     * @param content
     * @return null if failed to add such comment, otherwise the comment
     */
    Comment addComment(Long buildingId, Long commenterId, String content);

    /**
     * @param commentId
     * @return null if the comment does not exist, otherwise, a Comment instance
     */
    Comment getCommentById(Long commentId);

    /**
     * @param commentId
     * @return false if failed to delete the comment, otherwise, true
     */
    boolean deleteComment(Long commentId);

    /**
     * @param commentId id of the comment to be rejected
     * @return false if failed to reject the comment, otherwise, true
     */
    boolean rejectComment(Long commentId);

    /**
     * @param commentId id of the comment to be approved
     * @return false if failed to approve the comment, otherwise, true
     */
    boolean approveComment(Long commentId);

    /**
     * @param commentId
     * @return whether the comment with the given id exists
     */
    boolean commentExists(Long commentId);

    /**
     * @param commentId
     * @return null if the comment does not exist, otherwise, the status of the comment
     */
    CommentStatus getCommentStatus(Long commentId);

    /**
     * @param buildingId only return comments of the given building, null means disable this filter
     * @param commenterId only return comments of the given commenter, null means disable this filter
     * @param status only return comments of the given status, null means disable this filter
     * @return all comments that satisfy above requirements
     */
    List<Comment> getComments(Long buildingId, Long commenterId, CommentStatus status);

    /**
     * @param commentId
     * @return null if comment with the given id does not exist, otherwise, all photos of the comment
     */
    List<CommentPhoto> getCommentPhotos(Long commentId);

    /**
     * @param photoId
     * @return null if the photo does not exist, otherwise, a CommentPhoto instance
     */
    CommentPhoto getCommentPhotoById(Long photoId);

    /**
     * @param photoId
     * @return whether CommentPhoto with the given id exists
     */
    boolean commentPhotoExists(Long photoId);

    /**
     * @param commandId
     * @param photoPath
     * @return null if failed to insert the photo, otherwise, the id of the photo
     */
    Long addCommentPhoto(Long commandId, String photoPath);

    /**
     * @param photoId
     * @return false if failed to delete the photo, otherwise true
     */
    boolean deleteCommentPhoto(Long photoId);
}
