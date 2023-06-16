package com.sld.webstore.user.service;

import com.sld.webstore.user.dto.UserDto;
import com.sld.webstore.user.entity.UserEntity;
import com.sld.webstore.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    public void saveUser(UserDto userDto) {
        userRepository.save(UserEntity.fromUserDto(userDto));
    }
}
