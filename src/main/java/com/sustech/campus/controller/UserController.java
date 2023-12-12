package com.sustech.campus.controller;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
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
        User responseUser =  userService.getUserByName(user.getName());
        if(responseUser != null && user.getPassword()== responseUser.getPassword()) {
            String token=userService.changeToken(user.getId());
            return ResponseEntity.ok(token);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
//    @PostMapping( "/adminLogin")
//    public ResponseEntity<String> userLogin(User user){
//        User responseUser =  userService.getUserById(user.getId());
//        if(responseUser != null && user.getPassword()==responseUser.getPassword()) {
//            String token=userService.changeToken(user.getId());
//            return ResponseEntity.ok(token);
//        }else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PutMapping("/changeType/{id}")
    public ResponseEntity<String> changeUserType(@PathVariable Long id, @RequestBody UserType newType) {
        boolean success = userService.changeUserType(id, newType);
        if (success) {
            return ResponseEntity.ok("User type updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

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
