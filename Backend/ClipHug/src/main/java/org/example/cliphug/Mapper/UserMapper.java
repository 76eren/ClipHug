package org.example.cliphug.Mapper;


import lombok.RequiredArgsConstructor;
import org.example.cliphug.Dto.User.UserResponseDTO;
import org.example.cliphug.Model.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public UserResponseDTO fromEntity(User user) {

        return UserResponseDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
