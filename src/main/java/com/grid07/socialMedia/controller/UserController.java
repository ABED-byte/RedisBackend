package com.grid07.socialMedia.controller;

import com.grid07.socialMedia.dto.CreateUserRequest;
import com.grid07.socialMedia.entity.User;
import com.grid07.socialMedia.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // This method creates a new user and saves it to PostgreSQL.
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPremium(request.isPremium());
        return userRepository.save(user);
    }
}
