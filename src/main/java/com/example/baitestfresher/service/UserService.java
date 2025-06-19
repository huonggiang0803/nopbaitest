package com.example.baitestfresher.service;

import java.util.List;

import com.example.baitestfresher.dto.registerDTO;
import com.example.baitestfresher.dto.userResponse;
import com.example.baitestfresher.entity.User;

public interface UserService {
    String saveUser(registerDTO userRequestDTO);
    String loginUser(String username, String password);
    void deleteUser(long id, User currentUser);
    List<userResponse> getAllUsers();
    User findByUsername(String username) ;
    userResponse findById(Long id) ;
    userResponse findUserByKeyword(String keyword);
    userResponse updateUser(Long id, userResponse request);
}
