package com.example.springbootjpa.repository;

import com.example.springbootjpa.entity.Elective;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectiveRepository extends BaseRepository<Elective, Integer>{
    @Query("FROM Elective e")
    Optional<List<Elective>> list();

    @Query("SELECT e FROM Elective e WHERE e.student.id=:studentId AND e.course.id=:courseId")
    Optional<Elective> getElectivesByStudentIdAndCourseId(@Param("studentId")int studentId,@Param("courseId")int courseId);

    @Query("SELECT e FROM Elective e WHERE e.student.id=:id")
    Optional<List<Elective>> getElectivesStudentId(@Param("id")int id);

    @Query("SELECT e FROM Elective e WHERE e.course.id=:id")
    Optional<List<Elective>> getElectivesCourseId(@Param("id")int id);
}
