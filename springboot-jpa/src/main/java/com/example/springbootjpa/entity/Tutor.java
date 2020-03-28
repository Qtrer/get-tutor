package com.example.springbootjpa.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Tutor {
    @Id
    private int id;
    private String name;
    private String password;
    private int sumCount;
    private float remainScore;
    @OneToMany(mappedBy = "tutor")
    private List<Student> students;
    @OneToMany(mappedBy = "tutor")
    private List<Course> courses;

    @Column(columnDefinition = "timestamp default current_timestamp " +
            "on update current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime updateTime;
}
