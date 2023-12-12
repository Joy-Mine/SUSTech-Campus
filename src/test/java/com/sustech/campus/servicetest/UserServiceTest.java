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
            user.setId(this.userService.registerUser(user.getName(), user.getPassword(), user.getType()).getId());
            user.setToken(this.userService.getUserById(user.getId()).getToken());
            assertNotNull(user.getToken());
            assertNotNull(user.getId());
        }
    }

    @AfterEach
    void clean() {
        for (User user : this.users) {
            assertTrue(this.userService.deleteUser(user.getId()));
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
    void testGetUserById() {
        for (User user : this.users) {
            assertEquals(
                    this.userService.getUserById(user.getId()),
                    user
            );
        }
    }

    @Test
    @Order(3)
    void testGetUserType() {
        for (User user : this.users) {
            assertEquals(
                    this.userService.getUserType(user.getId()),
                    user.getType()
            );
        }
    }

    @Test
    @Order(4)
    void testUserExists() {
        for (User user : this.users) {
            assertTrue(this.userService.userExists(user.getName()));
            assertTrue(this.userService.userExists(user.getId()));
        }
    }

    @Test
    @Order(5)
    void testChangeUserType() {
        for (User user : this.users) {
            assertTrue(this.userService.changeUserType(
                    user.getId(),
                    UserType.MUTED
            ));
            assertEquals(
                    this.userService.getUserType(user.getId()),
                    UserType.MUTED
            );
        }
    }

    @Test
    @Order(6)
    void testCheckToken() {
        for (User user : this.users) {
            assertTrue(this.userService.checkToken(
                    user.getId(),
                    user.getToken()
            ));
            assertFalse(this.userService.checkToken(
                    user.getId(),
                    user.getToken() + "foo"
            ));
        }
    }

    @Test
    @Order(7)
    void testChangeToken() {
        for (User user : this.users) {
            String newToken = this.userService.changeToken(user.getId());
            assertNotNull(newToken);
            assertNotEquals(newToken, user.getToken());
            assertTrue(this.userService.checkToken(user.getId(), newToken));
        }
    }

    @Test
    @Order(8)
    void testChangeUserName() {
        for (User user : this.users) {
            assertTrue(this.userService.changeUserName(
                    user.getId(),
                    user.getName() + "_new"
            ));
            user.setName(user.getName() + "_new");
            assertEquals(
                    this.userService.getUserById(user.getId()),
                    user
            );
        }
    }

}
