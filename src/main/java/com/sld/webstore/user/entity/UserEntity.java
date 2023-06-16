package com.sld.webstore.user.entity;


import com.sld.webstore.user.dto.UserDto;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String password;

    public static UserEntity fromUserDto(UserDto userDto) {
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .build();
    }
}
