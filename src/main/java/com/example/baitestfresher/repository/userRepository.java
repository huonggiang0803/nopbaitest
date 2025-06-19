package com.example.baitestfresher.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.baitestfresher.entity.User;

@Repository
public interface userRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);
    Optional<User> findById(Long id);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    Optional<User> findByUserNameIgnoreCaseOrEmailIgnoreCase(String userName, String email);
}
