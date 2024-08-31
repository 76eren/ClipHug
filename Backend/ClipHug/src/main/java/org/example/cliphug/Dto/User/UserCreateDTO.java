package org.example.cliphug.Dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private String username; // Is an email
    private String password;
    private String firstName;
    private String lastName;
    private String pin;
}
