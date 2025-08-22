package com.oneonefive.PathNote.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oneonefive.PathNote.entity.User;
import com.oneonefive.PathNote.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/api/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}
