package com.longfish.jclogindemo.controller;

import com.longfish.jclogindemo.pojo.Result;
import com.longfish.jclogindemo.pojo.vo.UserInfoVO;
import com.longfish.jclogindemo.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userInfo")
@Tag(name = "用户信息")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Operation(summary = "我的信息")
    @GetMapping("/self")
    public Result<UserInfoVO> self() {
        UserInfoVO userInfoVO = userInfoService.self();
        return Result.success(userInfoVO);
    }
}
