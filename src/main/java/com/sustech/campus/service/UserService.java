package com.sustech.campus.service;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;

import java.util.List;

public interface UserService {
    /**
     * @param username
     * @return null if the user does not exist, otherwise a User instance
     */
    User getUserByName(String username);

    User getUserByToken(String token);

    /**
     *
     * @param userId
     * @return null if the user does not exist, otherwise a User instance
     */
    User getUserById(Long userId);

    /**
     * @param userId
     * @return null if the user does not exist, otherwise the type of the user
     */
    UserType getUserType(Long userId);

    /**
     * @param username
     * @return whether the user with the given username exists
     */
    boolean userExists(String username);

    /**
     * @param userId
     * @return whether the user with the given id exists
     */
    boolean userExists(Long userId);

    /**
     * @param userId
     * @param newName
     * @return false if failed to change the name of the user
     */
    boolean changeUserName(Long userId, String newName);

    /**
     * @param username
     * @param password
     * @param type
     * @return null if registration failed, otherwise, the id of the user
     */
    User registerUser(String username, String password, UserType type);

    /**
     * @param userId
     * @param newType
     * @return false if failed to change the type of the user, otherwise true
     */
    boolean changeUserType(Long userId, UserType newType);

    /**
     * @param userId
     * @return whether deletion succeed
     */
    boolean deleteUser(Long userId);

    /**
     * @param userId
     * @param token
     * @return whether the given token is the same as the token in the database
     */
    boolean checkToken(Long userId, String token);

    /**
     * change the token of the user with the given username
     * @param userId
     * @return null if failed to change the token, otherwise, the new token
     */
    String changeToken(Long userId);

    List<User> getAllUsers();

    boolean muteUser(String username);

    boolean unmuteUser(String username);
}
