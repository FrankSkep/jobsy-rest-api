package com.fran.jobsy.app.dto.user;

import com.fran.jobsy.app.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String lastname;
    private String firstname;
    private String country;
    private Role role;
}
