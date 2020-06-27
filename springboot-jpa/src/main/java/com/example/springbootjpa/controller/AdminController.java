package com.example.springbootjpa.controller;

import com.example.springbootjpa.entity.Student;
import com.example.springbootjpa.entity.Tutor;
import com.example.springbootjpa.entity.User;
import com.example.springbootjpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;

public class AdminController {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;

    @PostMapping("tutor")
    public Map addTutor(@Valid @RequestBody User user){
        Tutor tutor = new Tutor();
        if(user.getNumber()!=null && user.getName()!=null){
            User u = new User();
            u.setNumber(user.getNumber());
            u.setName(user.getName());
            u.setPassword(encoder.encode(String.valueOf(user.getNumber())));
            u.setRole(User.Role.TUTOR);
            tutor.setInstructedNumber(0);
            tutor.setTotalNumber(30);
            tutor.setReservedRange(50);
            tutor.setUser(u);
            userService.addTutor(tutor);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name, number cannot be empty.");
        }
        return Map.of(
                "tutor",tutor
        );
    }

    @DeleteMapping("tutor/{tid}")
    public Map deleteTotur(@PathVariable int tid){
        if(userService.getTutorById(tid)==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The tutor you want to delete does not exist.");
        }
        userService.deleteTutor(tid);
        return Map.of("massage", "Successful delete!");
    }

    @PostMapping("student")
    public Map addStudent(@Valid @RequestBody User user){
        Student student = new Student();
        if(user.getNumber() != null && user.getName() != null){
            User u = new User();
            u.setNumber(user.getNumber());
            u.setName(user.getName());
            u.setPassword(encoder.encode(String.valueOf(user.getNumber())));
            u.setRole(User.Role.TUTOR);
            student.setUser(u);
            userService.addStudent(student);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Name, number cannot be empty.");
        }
        return Map.of(
                "student",student
        );
    }
}