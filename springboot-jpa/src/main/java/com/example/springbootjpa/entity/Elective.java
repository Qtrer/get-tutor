package com.example.springbootjpa.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Elective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private  float score;
    @ManyToOne
    private Student student;
    @ManyToOne
    private Course course;
}
