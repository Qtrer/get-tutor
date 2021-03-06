package com.example.springbootjpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"electives", "directions"})
public class Student {
    @Id
    private int id;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @MapsId
    private User user;
    private float weightedScore;
    @ManyToOne
    private Tutor tutor;
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
    private List<Elective> electives;
    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
    private List<Direction> directions;

    @Column(columnDefinition = "timestamp default current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime insertTime;
    @Column(columnDefinition = "timestamp default current_timestamp " +
            "on update current_timestamp",
            insertable = false,
            updatable = false)
    private LocalDateTime updateTime;
}
