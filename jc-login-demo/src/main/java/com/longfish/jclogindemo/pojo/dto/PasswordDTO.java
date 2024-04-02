package com.longfish.jclogindemo.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordDTO {

    private String username;

    private String oldPassword;

    private String newPassword;

    private String code;
}
