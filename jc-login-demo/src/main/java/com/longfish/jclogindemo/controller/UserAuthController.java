package com.longfish.jclogindemo.controller;

import com.longfish.jclogindemo.pojo.Result;
import com.longfish.jclogindemo.pojo.dto.PasswordDTO;
import com.longfish.jclogindemo.pojo.dto.UserLoginDTO;
import com.longfish.jclogindemo.pojo.dto.UserRegDTO;
import com.longfish.jclogindemo.pojo.vo.UserLoginVO;
import com.longfish.jclogindemo.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
    @Parameters({
            @Parameter(name = "username", description = "用户名", required = true),
            @Parameter(name = "password", description = "密码", required = true)
    })
    public Result<UserLoginVO> login(String username, String password) {
        UserLoginDTO userLoginDTO = UserLoginDTO.builder().username(username).password(password).build();
        UserLoginVO userLoginVO = userAuthService.login(userLoginDTO);
        return Result.success(userLoginVO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    @Parameters({
            @Parameter(name = "username", description = "用户名", required = true),
            @Parameter(name = "password", description = "密码", required = true),
            @Parameter(name = "code", description = "验证码", required = true)
    })
    public Result login(String username, String password, String code) {
        UserRegDTO userRegDTO = UserRegDTO.builder().username(username).password(password).code(code).build();
        userAuthService.register(userRegDTO);
        return Result.success();
    }

    @Operation(summary = "忘记密码")
    @PostMapping("/forgot")
    @Parameters({
            @Parameter(name = "username", description = "用户名", required = true),
            @Parameter(name = "code", description = "验证码", required = true),
            @Parameter(name = "newPassword", description = "新密码", required = true)
    })
    public Result forgot(String username, String code, String newPassword) {
        PasswordDTO passwordDTO = PasswordDTO.builder().username(username).code(code).newPassword(newPassword).build();
        userAuthService.forgot(passwordDTO);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    @Parameters({
            @Parameter(name = "username", description = "用户名", required = true),
            @Parameter(name = "oldPassword", description = "旧密码", required = true),
            @Parameter(name = "newPassword", description = "新密码", required = true)
    })
    public Result password(String username, String oldPassword, String newPassword) {
        PasswordDTO passwordDTO = PasswordDTO.builder().username(username).oldPassword(oldPassword).newPassword(newPassword).build();
        userAuthService.password(passwordDTO);
        return Result.success();
    }
}
