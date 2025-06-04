package com.example.moviesbymood.repositories;

import com.example.moviesbymood.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String email);
    Optional<User> findByUserNickname(String nickname);
}
