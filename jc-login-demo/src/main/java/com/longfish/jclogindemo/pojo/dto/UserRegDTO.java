package com.longfish.jclogindemo.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegDTO {

    private Long id;

    private String username;

    private String password;

    private String code;

    private String createTime;

    private String updateTime;
}
