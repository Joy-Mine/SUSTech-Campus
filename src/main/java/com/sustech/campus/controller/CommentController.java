package com.sustech.campus.controller;

import com.sustech.campus.entity.Comment;
import com.sustech.campus.entity.CommentPhoto;
import com.sustech.campus.entity.User;
import com.sustech.campus.enums.CommentStatus;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.CommentService;
import com.sustech.campus.service.UserService;
import com.sustech.campus.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/comment")
public class CommentController {

    private final UserService userService;

    private final CommentService commentService;

    private static final Map<String, MediaType> MEDIA_TYPE_MAP;

    private static final String IMAGE_FOLDER = "src/main/resources/images/";

    static {
        // Initialize the media type map
        MEDIA_TYPE_MAP = new HashMap<>();
        MEDIA_TYPE_MAP.put("png", MediaType.IMAGE_PNG);
        MEDIA_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("gif", MediaType.IMAGE_GIF);
        // Add more mappings as necessary
    }

    @Autowired
    public CommentController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }


    private String getFileExtension(String filename) {
        if (filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        } else {
            return ""; // No extension found
        }
    }
    @GetMapping("/image/{subPath}")
    public ResponseEntity<byte[]> getImageAsResponseEntity(@PathVariable String subPath) throws IOException {
        String path = "images/" + subPath;
        Resource image = new ClassPathResource(path);
        byte[] imageContent = Files.readAllBytes(image.getFile().toPath());
        String fileExtension = getFileExtension(subPath);
        MediaType mediaType = MEDIA_TYPE_MAP.getOrDefault(fileExtension.toLowerCase(), MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().contentType(mediaType).body(imageContent);
    }

    @Access(level = UserType.USER)
    @PostMapping("/add")
    public ResponseEntity<String> addComment(
            HttpServletRequest request,
            @RequestParam("content") String content,
            @RequestParam("commenterId") String commenter,
            @RequestParam("buildingId") String building,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        Long commenterId = Long.parseLong(commenter);
        Long buildingId = Long.parseLong(building);
        String token = request.getHeader("TOKEN");
        if (token == null) {
            return ResponseEntity.badRequest().body("Illegal token");
        }
        User user = userService.getUserByToken(token);
        if (user == null || !user.getId().equals(commenterId)) {
            return ResponseEntity.badRequest().body("Illegal user");
        }
        if (user.getType() == UserType.MUTED) {
            return ResponseEntity.badRequest().body("User is muted");
        }
        Comment result = commentService.addComment(buildingId, commenterId, content);
        if (result == null) {
            return ResponseEntity.badRequest().body("Illegal parameters");
        }
        if (image != null && !image.isEmpty()) {
            String fileName;
            Path path;
            do {
                fileName = Utils.generateFileName(image.getName());
                path = Path.of(IMAGE_FOLDER, fileName);
            } while (Files.exists(path));
            Files.createDirectories(path.getParent());
            image.transferTo(path);
            commentService.addCommentPhoto(result.getId(), fileName);
        }
        return ResponseEntity.ok("Success");
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
        System.out.println(buildingId);
        List<Comment> comments = commentService.getComments(buildingId, null, CommentStatus.APPROVED);
        if (!comments.isEmpty()) {
            comments = comments.stream()
                    .sorted(Comparator.comparing(Comment::getTime).reversed())
                    .peek(e -> e.setCommenterName(userService.getUserById(e.getCommenterId()).getName()))
                    .peek(e -> e.setPhotos(
                            commentService.getCommentPhotos(e.getId()).stream()
                                    .peek(e1 -> e1.setPath("http://localhost:8082/comment/image/" + e1.getPath()))
                                    .sorted(Comparator.comparing(CommentPhoto::getId))
                                    .toList()
                    ))
                    .toList();
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
