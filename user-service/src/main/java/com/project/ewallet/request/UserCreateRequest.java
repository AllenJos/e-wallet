package com.project.ewallet.request;

import com.project.ewallet.model.User;
import com.project.ewallet.model.UserIdentifier;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber; //username

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private UserIdentifier userIdentifier;

    @NotBlank
    private String identifierValue;

    private String dob;

    private String country;

    public User to() {
        return User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .country(country)
                .dob(dob)
                .userIdentifier(userIdentifier)
                .identifierValue(identifierValue)
                .build();
    }
}
