package com.bankapp.dto.request;

import com.bankapp.entity.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private Long id;
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String email;
    private String password;
    private String phoneNumber;
    private String alternativePhoneNumber;
    private Role role;


}
