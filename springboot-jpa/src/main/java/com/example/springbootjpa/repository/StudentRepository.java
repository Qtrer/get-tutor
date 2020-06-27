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

    @Query("SELECT s FROM Student  s WHERE s.tutor.id=:id")
    Optional<List<Student>> getStudentsByTutorId (@Param("id")int id);

    @Query("SELECT s FROM Student  s WHERE s.user.number=:number")
    Optional<Student> getStudentsByUserNumber (@Param("number")int number);

    @Modifying
    @Query("UPDATE Student s SET s.tutor=:tutor WHERE s.id=:id")
    int updateTutor(@Param("tutor") Tutor tutor, @Param("id") int id);

    @Modifying
    @Query("UPDATE Student s SET s.weightedScore=:weightedGrade WHERE s.id=:id")
    int updateWeightedGrade(@Param("weightedScore") double weightedGrade, @Param("id") int id);

    Optional<Student> findById (int  id);
    void deleteById(int id);
}
