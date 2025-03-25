package com.fpt.enewsvn.controller;

import com.fpt.enewsvn.dto.request.user.CreateUserRequest;
import com.fpt.enewsvn.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/create")
    public boolean create(@RequestBody CreateUserRequest createUserRequest) {
        userService.create(createUserRequest);
        return true;
    }

}
