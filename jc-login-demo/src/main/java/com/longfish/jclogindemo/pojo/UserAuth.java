package com.longfish.jclogindemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuth {

    private Long id;

    private String username;

    private String password;

    private String createTime;

    private String updateTime;
}
