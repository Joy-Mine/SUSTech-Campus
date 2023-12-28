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
            building.setTag("");
            building.setLatitude(0);
            building.setLongitude(0);
            this.buildings.add(building);
            Long buildingId = this.buildingService.addBuilding(
                    building.getName(),
                    building.getTag(),
                    building.getDescription(),
                    building.getDetails(),
                    building.getLatitude(),
                    building.getLongitude()
            );
            building.setId(buildingId);
        }
        for (int i = 0; i < numUsers; ++i) {
            User user = new User();
            user.setName("test_user_" + i);
            user.setPassword("");
            user.setType(UserType.USER);
            user.setToken("");
            this.users.add(user);
            Long userId = this.userService.registerUser(
                    user.getName(),
                    user.getPassword(),
                    user.getType()
            ).getId();
            user.setId(userId);
            user.setToken(this.userService.getUserById(userId).getToken());
        }
        for (int i = 0; i < numComments; ++i) {
            Comment comment = new Comment();
            Building building = this.buildings.get(new Random().nextInt(this.buildings.size()));
            comment.setBuildingId(building.getId());
            User user = this.users.get(new Random().nextInt(this.users.size()));
            comment.setCommenterId(user.getId());
            comment.setStatus(CommentStatus.WAITING);
            comment.setContent("");
            this.comments.add(comment);
            Comment result = this.commentService.addComment(
                    comment.getBuildingId(),
                    comment.getCommenterId(),
                    comment.getContent()
            );
            assertNotNull(result);
            comment.setId(result.getId());
            comment.setTime(result.getTime());
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
            this.buildingService.deleteBuilding(building.getId());
        }
        for (User user : this.users) {
            this.userService.deleteUser(user.getId());
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
        List<Long> buildingIds = new ArrayList<>(this.buildings.stream().map(Building::getId).toList());
        List<Long> commenterIds = new ArrayList<>(this.users.stream().map(User::getId).toList());
        List<CommentStatus> commentStatuses = new ArrayList<>(List.of(
                CommentStatus.WAITING,
                CommentStatus.APPROVED,
                CommentStatus.REJECTED
        ));
        buildingIds.add(null);
        commenterIds.add(null);
        commentStatuses.add(null);
        for (Long buildingId : buildingIds) {
            for (Long commenterId : commenterIds) {
                for (CommentStatus commentStatus : commentStatuses) {
                    assertIterableEquals(
                            this.commentService.getComments(buildingId, commenterId, commentStatus).stream()
                                    .sorted(Comparator.comparing(Comment::getId)).toList(),
                            this.comments.stream()
                                    .filter(e -> buildingId == null || e.getBuildingId().equals(buildingId))
                                    .filter(e -> commenterId == null || e.getCommenterId().equals(commenterId))
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
