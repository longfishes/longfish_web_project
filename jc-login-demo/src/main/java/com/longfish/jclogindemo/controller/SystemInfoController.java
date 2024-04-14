package com.longfish.jclogindemo.controller;

import com.longfish.jclogindemo.pojo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "系统信息")
public class SystemInfoController {

    @Operation(summary = "系统图标")
    @GetMapping("/favicon.ico")
    public Result favouriteIco() {
        return Result.success("暂无信息");
    }

    @Operation(summary = "网站信息")
    @GetMapping("/")
    public Result info() {
        return Result.success("暂无信息");
    }
}
