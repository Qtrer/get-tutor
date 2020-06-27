package com.example.springbootjpa.component;

import com.example.springbootjpa.entity.Tutor;
import com.example.springbootjpa.entity.User;
import com.example.springbootjpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitializingComponent implements InitializingBean {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;

    @Override
    public void afterPropertiesSet() throws Exception {
        final int number = 2017214271;
        User user = userService.getUserByNumber(number);
        if (user == null) {
            User u = new User();
            u.setName("Qtrer");
            u.setNumber(number);
            u.setRole(User.Role.TUTOR);
            u.setPassword(encoder.encode(String.valueOf(number)));
            Tutor tutor = new Tutor();
            tutor.setUser(u);
            userService.addTutor(tutor);
        }
    }
}
