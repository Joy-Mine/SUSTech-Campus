package com.sustech.campus.service;

import com.sustech.campus.entity.User;
import com.sustech.campus.enums.UserType;

public interface UserService {
    /**
     * @param username
     * @return null if the user does not exist, otherwise a User instance
     */
    User getUserByName(String username);

    User getUserByToken(String token);

    /**
     * @param username
     * @return null if the user does not exist, otherwise the type of the user
     */
    UserType getUserType(String username);

    /**
     * @param username
     * @return whether the user with the given username exists
     */
    boolean userExists(String username);

    /**
     * @param username
     * @param password
     * @param type
     * @return null if registration failed, otherwise, the token of the user
     */
    String registerUser(String username, String password, UserType type);

    /**
     * @param username
     * @param newType
     * @return false if failed to change the type of the user, otherwise true
     */
    boolean changeUserType(String username, UserType newType);

    /**
     * @param username
     * @return whether deletion succeed
     */
    boolean deleteUser(String username);

    /**
     * @param username
     * @param token
     * @return whether the given token is the same as the token in the database
     */
    boolean checkToken(String username, String token);

    /**
     * change the token of the user with the given username
     * @param username
     * @return null if failed to change the token, otherwise, the new token
     */
    String changeToken(String username);
}
