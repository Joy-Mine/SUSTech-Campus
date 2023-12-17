package com.sustech.campus.controller;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByName(@PathVariable String username) {
        User user = userService.getUserByName(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        User responseUser = userService.registerUser(user.getName(), user.getPassword(), user.getType());
        if (responseUser != null) {
//            return ResponseEntity.ok("user.getToken()");
            System.out.println(responseUser.getToken());
            return ResponseEntity.ok(responseUser.getToken());
        } else {
            System.out.println("Registration failed: User already exists.");
            return ResponseEntity.badRequest().body("Registration failed: User already exists.");
        }
    }

    @PostMapping( "/login")
    public ResponseEntity<String> login(@RequestBody User user){
        user.setType(UserType.USER);
        User responseUser =  userService.getUserByName(user.getName());
        if(responseUser != null) {
            if(user.getPassword().equals(responseUser.getPassword())) {
                String token = userService.changeToken(responseUser.getId());
                System.out.println(token);
                return ResponseEntity.ok(token);
            }
            else {
                System.out.println("Wrong password.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");
            }
        }else {
            System.out.println("Failed.");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( "/adminlogin")
    public ResponseEntity<String> adminlogin(@RequestBody User user){
        user.setType(UserType.ADMIN);
//        System.out.println(user.toString());
        User responseUser =  userService.getUserByName(user.getName());
        if(responseUser != null) {
//            System.out.println(responseUser.toString());
            if(user.getPassword().equals(responseUser.getPassword())) {
                String token = userService.changeToken(responseUser.getId());
                System.out.println(token);
                return ResponseEntity.ok(token);
            }
            else {
                System.out.println("Wrong password.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong password");
            }
        }else {
            System.out.println("Failed.");
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/changeType/{id}")
    public ResponseEntity<String> changeUserType(@PathVariable Long id, @RequestBody UserType newType) {
        boolean success = userService.changeUserType(id, newType);
        if (success) {
            return ResponseEntity.ok("User type updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        if (success) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}
