package com.longfish.jclogindemo.controller;

import com.longfish.jclogindemo.pojo.Result;
import com.longfish.jclogindemo.pojo.dto.*;
import com.longfish.jclogindemo.pojo.vo.UserLoginVO;
import com.longfish.jclogindemo.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userAuth")
@Tag(name = "用户认证")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginVO userLoginVO = userAuthService.login(userLoginDTO);
        return Result.success(userLoginVO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserRegDTO userRegDTO) {
        userAuthService.register(userRegDTO);
        return Result.success();
    }

    @Operation(summary = "忘记密码")
    @PostMapping("/forgot")
    public Result forgot(@RequestBody PasswordForgetDTO dto) {
        PasswordDTO passwordDTO = PasswordDTO.builder().username(dto.getUsername()).code(dto.getCode()).newPassword(dto.getNewPassword()).build();
        userAuthService.forgot(passwordDTO);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result password(@RequestBody PasswordEditDTO dto) {
        PasswordDTO passwordDTO
                = PasswordDTO.builder()
                .username(dto.getUsername())
                .oldPassword(dto.getOldPassword())
                .newPassword(dto.getNewPassword())
                .build();
        userAuthService.password(passwordDTO);
        return Result.success();
    }
}
