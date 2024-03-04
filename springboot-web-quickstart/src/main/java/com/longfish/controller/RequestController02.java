package com.longfish.controller;

import com.longfish.pojo2.Result;
import com.longfish.pojo2.User;
import com.longfish.pojo2.User2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class RequestController02 {

    @RequestMapping("/simpleParam")
    public Result simpleParam(@RequestParam(name="name", required = false) String username, int age) {

        System.out.println(username+":"+age);
        return Result.success(username + ":" + age);
    }

    @RequestMapping("/simplePojo")
    public Result simplePojo(User u){

        System.out.println(u);
        return Result.success(u);
    }

    @RequestMapping("/complexPojo")
    public Result complexPojo(User2 u){

        System.out.println(u);
        return Result.success(u);
    }

    @RequestMapping("/arrayParam")
    public Result arrayParam(String[] hobbies){

        System.out.println(Arrays.toString(hobbies));
        return Result.success(hobbies);
    }

    @RequestMapping("/listParam")
    public Result listParam(@RequestParam List<String> hobbies){

        System.out.println(hobbies);
        return Result.success(hobbies);
    }

    @RequestMapping("/dataParam")
    public Result dataParam(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updateTime){

        System.out.println(updateTime);
        return Result.success(updateTime);
    }

    @RequestMapping("/jsonParam")
    public Result jsonParam(@RequestBody User2 u){

        System.out.println(u);
        return Result.success(u);
    }

    @RequestMapping("/path/{id}/{name}")
    public Result pathParam(@PathVariable Integer id, @PathVariable String name){

        System.out.println(id + ":" + name);
        return Result.success(id + ":" + name);
    }
}
