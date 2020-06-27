package com.example.springbootjpa;

import com.example.springbootjpa.intercepter.AdminInterceptor;
import com.example.springbootjpa.intercepter.LoginInterceptor;
import com.example.springbootjpa.intercepter.StudentInterceptor;
import com.example.springbootjpa.intercepter.TutorInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private TutorInterceptor tutorInterceptor;
    @Autowired
    private StudentInterceptor studentInterceptor;
    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login");
        registry.addInterceptor(tutorInterceptor)
                .addPathPatterns("/api/tutor**");
        registry.addInterceptor(studentInterceptor)
                .addPathPatterns("/api/student**");
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin**");

    }
}
