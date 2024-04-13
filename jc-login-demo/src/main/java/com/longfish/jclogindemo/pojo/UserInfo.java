package com.longfish.jclogindemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private String username;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;
}
