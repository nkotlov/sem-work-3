package com.example.moviesbymood.services;

import com.example.moviesbymood.dto.UserDto;
import com.example.moviesbymood.models.User;
import com.example.moviesbymood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto findByUsername(String email) {
        User u = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return toDto(u);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .userId(u.getUserId())
                .userEmail(u.getUserEmail())
                .userNickname(u.getUserNickname())
                .confirmed(u.isConfirmed())
                .userRole(u.getUserRole())
                .userRegistrationDate(u.getUserRegistrationDate())
                .oauthOnly(u.isOauthOnly())
                .avatarFilename(u.getAvatar() != null ? u.getAvatar().getFileInfoFilename() : null)
                .build();
    }
}
