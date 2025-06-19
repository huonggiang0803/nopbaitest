package com.example.baitestfresher.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.baitestfresher.config.JwtTokenUtil;
import com.example.baitestfresher.dto.registerDTO;
import com.example.baitestfresher.dto.userResponse;
import com.example.baitestfresher.entity.User;
import com.example.baitestfresher.repository.userRepository;
import com.example.baitestfresher.status.UserStatus;
import com.example.baitestfresher.status.UserType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImplement implements UserService{
    @Autowired
    private userRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public String saveUser(registerDTO userRequestDTO) {
        if (userRepository.existsByUserName(userRequestDTO.getUserName())){
            log.warn("Tên đăng nhập đã tồn tại: {}", userRequestDTO.getUserName());
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Email đã được sử dụng: {}", userRequestDTO.getUserName());
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }
    
        User user = User.builder()
        .name(userRequestDTO.getName())
        .userName(userRequestDTO.getUserName())
        .passWord(passwordEncoder.encode(userRequestDTO.getPassWord()))
        .email(userRequestDTO.getEmail())
        .phoneNumber(userRequestDTO.getPhoneNumber())
        .isDeleted((byte) 0)
        .avatar(userRequestDTO.getAvatar())
        .status(UserStatus.INACTIVE)
        .type(UserType.valueOf(userRequestDTO.getType().toUpperCase()))
        .build();
        userRepository.save(user);
        return "Đăng kí thành công";
    }

    @Override
    public String loginUser(String username, String password) {
        Optional<User> usOptional = userRepository.findByUserName(username);
        if (usOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Người dùng không tồn tại!");
        }
        User user = usOptional.get();
        if (user.getIsDeleted() == 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản của bạn đã bị vô hiệu hóa, vui lòng liên hệ hỗ trợ!");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) { 
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mật khẩu không chính xác!");
        }
        if (user.getStatus() != UserStatus.ACTIVE) {
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

        User existingUser = usOptional.get();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public void deleteUser(long id, User currentUser) {
     if (currentUser.getType() != UserType.ADMIN) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa người dùng!");
    }
    
    User userToDelete = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Người dùng không tìm thấy"));
        userToDelete.setIsDeleted((byte) 1);
        userToDelete.setStatus(UserStatus.DELETED);
        userRepository.save(userToDelete);
    }

   @Override
    public List<userResponse> getAllUsers() {
    List<User> users = userRepository.findAll();
    List<userResponse> responseList = users.stream()
        .map(user -> {
            userResponse res = new userResponse();
            res.setId(user.getId());
            res.setName(user.getName());
            res.setUserName(user.getUsername());
            res.setEmail(user.getEmail());
            res.setPhoneNumber(user.getPhoneNumber());
            res.setAvatar(user.getAvatar());
            res.setStatus(UserStatus.ACTIVE);
            return res;
        })
        .collect(Collectors.toList());
    return responseList;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUserName(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Người dùng không tìm thấy"));
    }

    @Override
    public userResponse findUserByKeyword(String keyword) {
        User user;
        try {
            Long id = Long.parseLong(keyword);
            user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        } catch (NumberFormatException ex) {
            user = userRepository.findByUserNameIgnoreCaseOrEmailIgnoreCase(keyword, keyword)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùn: " + keyword));
        }
        userResponse response = new userResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());

        return response;
    }

    @Override
    public userResponse updateUser(Long id, userResponse request) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng với ID = " + id));
        user.setName(request.getName());
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAvatar(request.getAvatar());
        user.setStatus(UserStatus.ACTIVE); 

        User savedUser = userRepository.save(user);

        userResponse response = new userResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setUserName(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setPhoneNumber(savedUser.getPhoneNumber());
        response.setAvatar(savedUser.getAvatar());
        response.setStatus(savedUser.getStatus());

        return response;
    }

    @Override
    public userResponse findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        userResponse response = new userResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setUserName(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());

        return response;
    }
  

}
