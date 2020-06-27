package com.example.springbootjpa.intercepter;

import com.example.springbootjpa.component.RequestComponent;
import com.example.springbootjpa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class StudentInterceptor implements HandlerInterceptor {
    @Autowired
    private RequestComponent requestComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        User.Role role = requestComponent.getRole();
        if (role.equals(User.Role.STUDENT) || role.equals(User.Role.ADMIN)){
        }else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Without permission");
        }
        return true;
    }
}

