package com.example.springbootjpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"courses", "students"})
public class Tutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @MapsId
    private User user;
    @PositiveOrZero
    private Integer instructedNumber;
    @PositiveOrZero
    private Integer totalNumber;
    @PositiveOrZero
    private Integer reservedRange;
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
