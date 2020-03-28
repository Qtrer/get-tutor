package com.example.springbootjpa.repository;

import com.example.springbootjpa.entity.Student;
import com.example.springbootjpa.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends BaseRepository<Student, Integer> {
    @Query("SELECT s FROM Student s")
    Optional<List<Student>> list();

    @Modifying
    @Query("UPDATE Student s SET s.password=:password WHERE s.id=:id")
    int updatePassword(@Param("password")String password, @Param("id") int id);

    @Modifying
    @Query("UPDATE Student s SET s.tutor=:tutor WHERE s.id=:id")
    int updateTutor(@Param("tutor") Tutor tutor, @Param("id") int id);

    Optional<List<Student>> findByName(String name);
    Optional<Student> findByStudentAccount(String studentAccount);
    Optional<Student> findById (int  id);
    void deleteByName(String name);
    void deleteByStudentAccount(String studentAccount);
}
