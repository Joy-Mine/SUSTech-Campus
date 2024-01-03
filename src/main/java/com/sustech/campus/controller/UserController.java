package com.sustech.campus.controller;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Access(level = UserType.ADMIN)
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
        System.out.println(user.getName()+user.getPassword()+user.getType());
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

    @Access(level = UserType.ADMIN)
    @PostMapping("/batchregister")
    public ResponseEntity<List<String>> batchRegisterUser(@RequestBody List<User> batchUsers){
        List<String> registeredUsersName=new ArrayList<>();
        for(User user : batchUsers){
            User responseUser = userService.registerUser(user.getName(), user.getPassword(), UserType.USER);
            if(responseUser!=null){
                registeredUsersName.add(responseUser.getName());
            }
        }
        return ResponseEntity.ok(registeredUsersName);
    }

    @Access(level = UserType.USER)
    @PostMapping("canComment")
    public ResponseEntity<Boolean> checkUserCanComment(@RequestBody String id){
        User responseUser =  userService.getUserById(Long.valueOf(id));
        if (responseUser != null && responseUser.getType() == UserType.USER){
            boolean response = true;
            return ResponseEntity.ok(response);
        }else if (responseUser != null && responseUser.getType() == UserType.MUTED){
            boolean reponse = false;
            return ResponseEntity.ok(reponse);
        }else {
            System.out.println("没有该用户");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping( "/userlogin")
    public ResponseEntity<Map<String, Object>> userlogin(@RequestBody User user){
//        user.setType(UserType.USER);
        User responseUser =  userService.getUserByName(user.getName());
        if(responseUser != null && responseUser.getType()==UserType.USER) {
            if(user.getPassword().equals(responseUser.getPassword())) {
                String token = userService.changeToken(responseUser.getId());
                System.out.println(responseUser.getId());
                Map<String, Object> responseMap = new HashMap<>();
                String stringId = responseUser.getId().toString();
                responseMap.put("token", token);
                responseMap.put("name", responseUser.getName().toString());
                responseMap.put("id", stringId);
                System.out.println(responseUser.getId());
                return ResponseEntity.ok(responseMap);
            }
            else {
                System.out.println("Wrong password.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }else {
            System.out.println("Failed.");
            return ResponseEntity.notFound().build();
        }
    }

    //deprecated
    @PostMapping( "/adminlogin")
    public ResponseEntity<Map<String, Object>> adminlogin(@RequestBody User user){
//        user.setType(UserType.ADMIN);
        System.out.println(user.toString());
        User responseUser =  userService.getUserByName(user.getName());
        if(responseUser != null && responseUser.getType()==UserType.ADMIN) {
            System.out.println(responseUser.toString());
            if(user.getPassword().equals(responseUser.getPassword())) {
                String token = userService.changeToken(responseUser.getId());
                System.out.println(token);
                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("token", token);
                responseMap.put("name", responseUser.getName());
                responseMap.put("id", responseUser.getId());
                return ResponseEntity.ok(responseMap);
            }
            else {
                System.out.println("Wrong password.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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

    @Access(level = UserType.ADMIN)
    @GetMapping("/allusers")
    public ResponseEntity<List<String[]>> getAllUsers(){
        List<User> allUsers=userService.getAllUsers();
        List<String[]> neededUsers=new ArrayList<>();
        for(User user : allUsers){
            if(user.getType()==UserType.ADMIN)
                continue;
            String[] tmp=new String[3];
            tmp[0]=user.getName();
            tmp[1]=user.getPassword();
            tmp[2]=user.getType().toString();
            neededUsers.add(tmp);
        }
        if (allUsers!=null) {
            return ResponseEntity.ok(neededUsers);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/mute")
    public boolean mute(@RequestBody User user){
        String username=user.getName();
        return userService.muteUser(username);
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/unmute")
    public boolean unmute(@RequestBody User user){
        String username=user.getName();
        return userService.unmuteUser(username);
    }
}
