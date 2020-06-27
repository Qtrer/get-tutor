package com.example.springbootjpa.controller;


import com.example.springbootjpa.component.RequestComponent;
import com.example.springbootjpa.entity.User;
import com.example.springbootjpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user/")
public class UserController {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestComponent requestComponent;


    @PatchMapping("settings/password")
    public User updatePassword(@RequestBody Map<String, String> pwd) {
        String oldPwd = pwd.get("oldPassword");
        String newPwd = pwd.get("newPassword");
        String confirmNewPwd = pwd.get("confirmNewPassword");

        if (!encoder.matches(oldPwd,
                userService.getUserByID(requestComponent.getUid()).getPassword()))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The old password you entered was incorrect!");
        }
        if (!newPwd.equals(confirmNewPwd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You enter the password differently!");
        }
        User user = userService.updateUserPassword(requestComponent.getUid(),newPwd);
        return user;
    }
}
