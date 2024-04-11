package com.longfish.jclogindemo.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordForgetDTO {

    private String username;

    private String code;

    private String newPassword;
}
