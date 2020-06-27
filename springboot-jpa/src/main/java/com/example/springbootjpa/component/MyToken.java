package com.example.springbootjpa.component;

import com.example.springbootjpa.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyToken {
    //Avoid hard coding
    public static final String AUTHORIZATION = "authorization";
    public static final String UID = "uid";
    public static final String ROLE = "role";

    private int uid;
    private User.Role role;
}
