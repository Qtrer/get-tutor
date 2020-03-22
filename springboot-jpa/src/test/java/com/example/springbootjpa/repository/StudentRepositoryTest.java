package com.example.springbootjpa.repository;

import com.example.springbootjpa.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Slf4j
@Transactional
@Rollback(value = false)
public class StudentRepositoryTest {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private EntityManager manager;
    @Test
    public void test_addStudent(){
        Student student = new Student();
        student.setId(2017224492);
        student.setName("Laner");
        studentRepository.save(student);
        log.debug("111");
    }
    @Test
    public void test_refresh(){
        Student student = new Student();
        student.setId(2017224492);
        manager.persist(student);
        log.debug("{}", student.getName());
    }
}
