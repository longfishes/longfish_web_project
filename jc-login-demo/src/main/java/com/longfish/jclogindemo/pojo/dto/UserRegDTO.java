package com.longfish.jclogindemo.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegDTO {

    @JsonIgnore
    private Long id;

    private String username;

    private String password;

    private String code;

    @JsonIgnore
    private String createTime;

    @JsonIgnore
    private String updateTime;
}
