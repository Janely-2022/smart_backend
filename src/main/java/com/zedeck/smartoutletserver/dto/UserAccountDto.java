package com.zedeck.smartoutletserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDto {
    private String fullname;
    private String username;
    private String userRole;
    private String password;

}

