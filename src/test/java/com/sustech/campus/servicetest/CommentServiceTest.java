package com.sustech.campus.servicetest;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.Comment;
import com.sustech.campus.entity.CommentPhoto;
import com.sustech.campus.entity.User;
import com.sustech.campus.enums.CommentStatus;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.BuildingService;
import com.sustech.campus.service.CommentService;
import com.sustech.campus.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommentServiceTest {
    private BuildingService buildingService;

    private CommentService commentService;

    private UserService userService;

    private List<User> users;

    private List<Building> buildings;

    private List<Comment> comments;

    private List<CommentPhoto> commentPhotos;

    @Autowired
    public CommentServiceTest(UserService userService, BuildingService buildingService, CommentService commentService) {
        this.userService = userService;
        this.buildingService = buildingService;
        this.commentService = commentService;

        this.users = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.commentPhotos = new ArrayList<>();
    }

    @BeforeEach
    void insert() {
        int numUsers = 2;
        int numBuildings = 2;
        int numComments = 5;
        int numCommentPhotos = 10;
        for (int i = 0; i < numBuildings; ++i) {
            Building building = new Building();
            building.setName("test_building_" + i);
            building.setDescription("");
            building.setDetails("");
            building.setLatitude(0);
            building.setLongitude(0);
            this.buildings.add(building);
            this.buildingService.addBuilding(
                    building.getName(),
                    building.getDescription(),
                    building.getDetails(),
                    building.getLatitude(),
                    building.getLongitude()
            );
        }
        for (int i = 0; i < numUsers; ++i) {
            User user = new User();
            user.setName("test_user_" + i);
            user.setPassword("");
            user.setType(UserType.USER);
            user.setToken("");
            this.users.add(user);
            String token = this.userService.registerUser(
                    user.getName(),
                    user.getPassword(),
                    user.getType()
            );
            user.setToken(token);
        }
        for (int i = 0; i < numComments; ++i) {
            Comment comment = new Comment();
            comment.setBuilding("test_building_" + new Random().nextInt(numBuildings));
            comment.setCommenter("test_user_" + new Random().nextInt(numUsers));
            comment.setStatus(CommentStatus.WAITING);
            comment.setContent("");
            this.comments.add(comment);
            Long commentId = this.commentService.addComment(
                    comment.getBuilding(),
                    comment.getCommenter(),
                    comment.getContent()
            );
            assertNotNull(commentId);
            comment.setId(commentId);
        }
        for (int i = 0; i < numCommentPhotos; ++i) {
            CommentPhoto photo = new CommentPhoto();
            Comment comment = this.comments.get(new Random().nextInt(this.comments.size()));
            photo.setPath("/foo/bar" + i);
            photo.setCommentId(comment.getId());
            this.commentPhotos.add(photo);
            Long photoId = this.commentService.addCommentPhoto(
                    photo.getCommentId(),
                    photo.getPath()
            );
            assertNotNull(photoId);
            photo.setId(photoId);
        }
    }

    @AfterEach
    void clean() {
        for (CommentPhoto photo : this.commentPhotos) {
            assertTrue(this.commentService.deleteCommentPhoto(photo.getId()));
        }
        for (Comment comment : this.comments) {
            assertTrue(this.commentService.deleteComment(comment.getId()));
        }
        for (Building building : this.buildings) {
            this.buildingService.deleteBuilding(building.getName());
        }
        for (User user : this.users) {
            this.userService.deleteUser(user.getName());
        }
    }

    @Test
    @Order(1)
    void testGetCommentById() {
        for (Comment comment : this.comments) {
            assertEquals(
                    this.commentService.getCommentById(comment.getId()),
                    comment
            );
        }
    }

    @Test
    @Order(2)
    void testGetCommentStatus() {
        for (Comment comment : this.comments) {
            assertEquals(
                    this.commentService.getCommentStatus(comment.getId()),
                    comment.getStatus()
            );
        }
    }

    @Test
    @Order(3)
    void testRejectComment() {
        for (Comment comment : this.comments) {
            assertTrue(this.commentService.rejectComment(comment.getId()));
            assertEquals(
                    this.commentService.getCommentStatus(comment.getId()),
                    CommentStatus.REJECTED
            );
            assertFalse(this.commentService.rejectComment(comment.getId()));
        }
    }

    @Test
    @Order(4)
    void testApproveComment() {
        for (Comment comment : this.comments) {
            assertTrue(this.commentService.approveComment(comment.getId()));
            assertEquals(
                    this.commentService.getCommentStatus(comment.getId()),
                    CommentStatus.APPROVED
            );
            assertFalse(this.commentService.approveComment(comment.getId()));
        }
    }

    @Test
    @Order(5)
    void testCommentExists() {
        for (Comment comment : this.comments) {
            assertTrue(this.commentService.commentExists(comment.getId()));
        }
    }

    @Test
    @Order(6)
    void testGetComments() {
        List<String> buildingNames = new ArrayList<>(this.buildings.stream().map(Building::getName).toList());
        List<String> commenterNames = new ArrayList<>(this.users.stream().map(User::getName).toList());
        List<CommentStatus> commentStatuses = new ArrayList<>(List.of(
                CommentStatus.WAITING,
                CommentStatus.APPROVED,
                CommentStatus.REJECTED
        ));
        buildingNames.add(null);
        commenterNames.add(null);
        commentStatuses.add(null);
        for (String buildingName : buildingNames) {
            for (String commenterName : commenterNames) {
                for (CommentStatus commentStatus : commentStatuses) {
                    assertIterableEquals(
                            this.commentService.getComments(buildingName, commenterName, commentStatus).stream()
                                    .sorted(Comparator.comparing(Comment::getId)).toList(),
                            this.comments.stream()
                                    .filter(e -> buildingName == null || e.getBuilding().equals(buildingName))
                                    .filter(e -> commenterName == null || e.getCommenter().equals(commenterName))
                                    .filter(e -> commentStatus == null || e.getStatus() == commentStatus)
                                    .sorted(Comparator.comparing(Comment::getId)).toList()
                    );
                }
            }
        }
    }

    @Test
    @Order(7)
    void testGetCommentPhotos() {
        for (Comment comment : this.comments) {
            assertIterableEquals(
                    this.commentService.getCommentPhotos(comment.getId()).stream()
                            .sorted(Comparator.comparing(CommentPhoto::getId)).toList(),
                    this.commentPhotos.stream().filter(e -> e.getCommentId().equals(comment.getId()))
                            .sorted(Comparator.comparing(CommentPhoto::getId)).toList()
            );
        }
    }


    @Test
    @Order(8)
    void testGetCommentPhotoById() {
        for (CommentPhoto photo : this.commentPhotos) {
            assertEquals(
                    this.commentService.getCommentPhotoById(photo.getId()),
                    photo
            );
        }
    }

    @Test
    @Order(9)
    void testCommentPhotoExists() {
        for (CommentPhoto photo : this.commentPhotos) {
            assertTrue(this.commentService.commentPhotoExists(photo.getId()));
        }
    }
}
