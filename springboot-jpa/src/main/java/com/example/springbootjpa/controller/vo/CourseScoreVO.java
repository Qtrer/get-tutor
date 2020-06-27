package com.example.springbootjpa.controller.vo;

import com.example.springbootjpa.entity.Course;
import lombok.Data;

@Data
public class CourseScoreVO {
    private Course course;
    private float score;
    private int sid;
}
