package com.sustech.campus.servicetest;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {
    private final UserService userService;
    private User adminUser, normalUser;
    @Autowired
    public UserServiceTest(UserService userService) {
        this.userService = userService;
        this.adminUser = new User();
        this.adminUser.setName("test_admin");
        this.adminUser.setPassword("test_admin_password");
        this.adminUser.setType(UserType.ADMIN);

        this.normalUser = new User();
        this.normalUser.setName("test_user");
        this.normalUser.setPassword("test_user_password");
        this.normalUser.setType(UserType.USER);
    }

    @BeforeEach
    void clean() {
        this.userService.deleteUser(this.adminUser.getName());
        this.userService.deleteUser(this.normalUser.getName());
    }

    @Test
    @Order(1)
    void test() {
        User[] users = new User[2];
        users[0] = this.adminUser;
        users[1] = this.normalUser;

        for (User user : users) {
            assertFalse(this.userService.userExists(user.getName()));
            assertNull(this.userService.getUserByName(user.getName()));
        }
        for (User user : users) {
            assertTrue(this.userService.registerUser(user.getName(), user.getPassword(), user.getType()));
            assertNotNull(this.userService.getUserByName(user.getName()));
            assertEquals(
                    this.userService.getUserByName(user.getName()).getName(),
                    user.getName()
            );
            assertEquals(
                    this.userService.getUserByName(user.getName()).getType(),
                    user.getType()
            );
            assertEquals(
                    this.userService.getUserByName(user.getName()).getPassword(),
                    user.getPassword()
            );
        }
        for (User user: users) {
            assertTrue(this.userService.deleteUser(user.getName()));
            assertFalse(this.userService.userExists(user.getName()));
            assertNull(this.userService.getUserByName(user.getName()));
        }
    }

    @Test
    @Order(2)
    void testChangeUserType() {
        assertFalse(this.userService.userExists(this.normalUser.getName()));
        assertTrue(this.userService.registerUser(
                this.normalUser.getName(),
                this.normalUser.getPassword(),
                this.normalUser.getType()
        ));
        assertEquals(
                this.userService.getUserByName(this.normalUser.getName()).getType(),
                UserType.USER
        );
        assertTrue(this.userService.changeUserType(
                this.normalUser.getName(),
                UserType.MUTED
        ));
        assertEquals(
                this.userService.getUserByName(this.normalUser.getName()).getType(),
                UserType.MUTED
        );
        assertTrue(this.userService.deleteUser(this.normalUser.getName()));
        assertFalse(this.userService.userExists(this.normalUser.getName()));
    }

}
