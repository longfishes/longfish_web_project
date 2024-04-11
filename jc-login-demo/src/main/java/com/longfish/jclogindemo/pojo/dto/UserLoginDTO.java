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
public class UserLoginDTO {

    @JsonIgnore
    private String id;

    private String username;

    private String password;

    @JsonIgnore
    private String updateTime;
}
