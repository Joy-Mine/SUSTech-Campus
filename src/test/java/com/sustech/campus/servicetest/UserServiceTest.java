package com.sustech.campus.servicetest;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    private final UserService userService;

    private List<User> users;

    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
        this.users = new ArrayList<>();

        User adminUser = new User();
        adminUser.setName("test_admin");
        adminUser.setPassword("test_admin_password");
        adminUser.setType(UserType.ADMIN);
        users.add(adminUser);

        User normalUser = new User();
        normalUser.setName("test_user");
        normalUser.setPassword("test_user_password");
        normalUser.setType(UserType.USER);
        users.add(normalUser);
    }

    @BeforeEach
    void insert() {
        for (User user : this.users) {
            user.setToken(this.userService.registerUser(user.getName(), user.getPassword(), user.getType()));
            assertNotNull(user.getToken());
        }
    }

    @AfterEach
    void clean() {
        for (User user : this.users) {
            assertTrue(this.userService.deleteUser(user.getName()));
        }
    }

    @Test
    @Order(1)
    void testGetUserByName() {
        for (User user : this.users) {
            assertEquals(
                    this.userService.getUserByName(user.getName()),
                    user
            );
        }
    }

    @Test
    @Order(2)
    void testGetUserType() {
        for (User user : this.users) {
            assertEquals(
                    this.userService.getUserType(user.getName()),
                    user.getType()
            );
        }
    }

    @Test
    @Order(3)
    void testUserExists() {
        for (User user : this.users) {
            assertTrue(this.userService.userExists(user.getName()));
        }
    }

    @Test
    @Order(3)
    void testChangeUserType() {
        for (User user : this.users) {
            assertTrue(this.userService.changeUserType(
                    user.getName(),
                    UserType.MUTED
            ));
            assertEquals(
                    this.userService.getUserType(user.getName()),
                    UserType.MUTED
            );
        }
    }

    @Test
    @Order(4)
    void testCheckToken() {
        for (User user : this.users) {
            assertTrue(this.userService.checkToken(
                    user.getName(),
                    user.getToken()
            ));
            assertFalse(this.userService.checkToken(
                    user.getName(),
                    user.getToken() + "foo"
            ));
        }
    }

    @Test
    @Order(5)
    void testChangeToken() {
        for (User user : this.users) {
            String newToken = this.userService.changeToken(user.getName());
            assertNotNull(newToken);
            assertNotEquals(newToken, user.getToken());
            assertTrue(this.userService.checkToken(user.getName(), newToken));
        }
    }
}
