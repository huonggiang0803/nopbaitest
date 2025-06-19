package com.example.baitestfresher.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.baitestfresher.dto.loginDTO;
import com.example.baitestfresher.dto.registerDTO;
import com.example.baitestfresher.dto.userResponse;
import com.example.baitestfresher.entity.User;
import com.example.baitestfresher.repository.userRepository;
import com.example.baitestfresher.service.UserService;
import com.example.baitestfresher.status.UserStatus;
import com.example.baitestfresher.status.UserType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final userRepository userRepository;
    @Autowired
    private UserService us;

    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUser(@Valid @RequestBody registerDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            String result = us.saveUser(dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/loginUser")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody loginDTO dto) {
        try {
            String token = us.loginUser(dto.getUserName(), dto.getPassWord());
            User user = us.findByUsername(dto.getUserName());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getType().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Bạn chưa đăng nhập!"));
        }
        currentUser.setStatus(UserStatus.INACTIVE);
        userRepository.save(currentUser);

        return ResponseEntity.ok(Collections.singletonMap("message", "Đăng xuất thành công."));
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null || currentUser.getType() != UserType.ADMIN) {
            Map<String, String> body = new HashMap<>();
            body.put("message", "Bạn không có quyền xem người dùng!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }
    List<userResponse> users = us.getAllUsers();
    return ResponseEntity.ok(users);
    }
 
    @GetMapping("/getUserId/{id}")
    public ResponseEntity<userResponse> getUserById(@PathVariable Long id){
        userResponse response = us.findById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = us.findByUsername(userDetails.getUsername());

        if (currentUser.getType() != UserType.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa người dùng!");
        }
        us.deleteUser(id, currentUser);
        return ResponseEntity.ok("Tài khoản đã bị vô hiệu hóa");
    }

    @GetMapping("/findUser")
    public ResponseEntity<userResponse> findUser(@RequestParam String keyword) {
        userResponse user = us.findUserByKeyword(keyword);
        return ResponseEntity.ok(user);
    }

    @PutMapping("updateUser/{id}")
    public ResponseEntity<userResponse> updateUser(
            @PathVariable Long id,
            @RequestBody userResponse request) {
        userResponse updatedUser = us.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }
    

    
    




}
