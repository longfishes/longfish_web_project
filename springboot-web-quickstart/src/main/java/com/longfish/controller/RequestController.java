package com.longfish.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class RequestController {

//    @RequestMapping("/simpleParam")
//    public String simpleParam(String name, int age) {
//
//        return name + "今年" + age + "岁";
//    }

    /*@RequestMapping("/simpleParam")
    public String simpleParam(@RequestParam(name="name", required = false) String username, int age) {

        System.out.println(username+":"+age);
        return "OK";
    }

    @RequestMapping("/simplePojo")
    public String simplePojo(User u){

        System.out.println(u);
        return "OK";
    }

    @RequestMapping("/complexPojo")
    public String complexPojo(User2 u){

        System.out.println(u);
        return "OK";
    }

    @RequestMapping("/arrayParam")
    public String[] arrayParam(String[] hobbies){

        System.out.println(Arrays.toString(hobbies));
        return hobbies;
    }

    @RequestMapping("/listParam")
    public String listParam(@RequestParam List<String> hobbies){

        System.out.println(hobbies);
        return "OK";
    }

    @RequestMapping("/dataParam")
    public String dataParam(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updateTime){

        System.out.println(updateTime);
        return "OK";
    }

    @RequestMapping("/jsonParam")
    public String jsonParam(@RequestBody User2 u){

        System.out.println(u);
        return "OK";
    }

    @RequestMapping("/path/{id}/{name}")
    public String pathParam(@PathVariable Integer id, @PathVariable String name){

        System.out.println(id + ":" + name);
        return "OK";
    }*/
}
