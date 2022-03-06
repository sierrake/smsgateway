package com.kesierra.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kesierra.entity.Session;
import com.kesierra.entity.User;
import com.kesierra.service.UserService;
import com.kesierra.vo.Reponse;
import com.kesierra.vo.UserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/auth/user"})
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = {"/register"}, produces = {"application/json"})
    public Reponse register(@RequestBody User user){
        //reponse = registerService.register(user);
        return userService.register(user);
    }

    @PostMapping(value = {"/login"}, produces = {"application/json"})
    public String login(@RequestBody User user){
        return JSONObject.toJSONString(userService.login(user));
    }

    @PostMapping(value = {"/logout"}, produces = {"application/json"})
    public String logout(@RequestBody Session session){
        return JSONObject.toJSONString(userService.logout(session));
    }
}
