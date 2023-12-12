package com.sustech.campus.controller;

import com.sustech.campus.entity.Comment;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @Access(level = UserType.USER)
    @PostMapping("/add")
    public ResponseEntity<Long> addComment(@RequestBody Comment comment) {
        Long commentId = commentService.addComment(comment.getBuildingId(), comment.getCommenterId(), comment.getContent());
        if (commentId != null) {
            return ResponseEntity.ok(commentId);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @Access(level = UserType.ADMIN)
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        boolean success = commentService.deleteComment(commentId);
        if (success) {
            return ResponseEntity.ok("Comment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/approve/{commentId}")
    public ResponseEntity<String> approveComment(@PathVariable Long commentId) {
        boolean success = commentService.approveComment(commentId);
        if (success) {
            return ResponseEntity.ok("Comment approved.");
        } else {
            return ResponseEntity.badRequest().body("Invalid comment ID or comment is not in a pending state.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/reject/{commentId}")
    public ResponseEntity<String> rejectComment(@PathVariable Long commentId) {
        boolean success = commentService.rejectComment(commentId);
        if (success) {
            return ResponseEntity.ok("Comment rejected.");
        } else {
            return ResponseEntity.badRequest().body("Invalid comment ID or comment is not in a pending state.");
        }
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<Comment>> getCommentsByBuilding(@PathVariable Long buildingId) {
        List<Comment> comments = commentService.getComments(buildingId, null, null);
        if (!comments.isEmpty()) {
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
