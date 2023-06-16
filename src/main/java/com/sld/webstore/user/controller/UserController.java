package com.sld.webstore.user.controller;

import com.sld.webstore.user.dto.UserDto;
import com.sld.webstore.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webstore/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;
    @GetMapping
    public UserDto getUser() {
        return UserDto.builder()
                .name("name")
                .surname("surname").build();
    }

    @PostMapping
    public void postUser(@RequestBody UserDto userDto) {
        userService.saveUser(userDto);
    }

}
