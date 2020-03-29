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
    Optional<Elective> getElective(@Param("studentId")int studentId, @Param("courseId")int courseId);

    @Query("SELECT e FROM Elective e WHERE e.student.name=:name")
    Optional<List<Elective>> getElectivesByStudentName(@Param("name")String name);

    @Query("SELECT e FROM Elective e WHERE e.student.studentAccount=:studentAccount")
    Optional<List<Elective>> getElectivesByStudentAccount(@Param("name")String name);

    @Query("SELECT e FROM Elective e WHERE e.student.id=:id")
    Optional<List<Elective>> getElectivesStudentId(@Param("id")int id);

    @Query("SELECT e FROM Elective e WHERE e.course.name=:name")
    Optional<List<Elective>> getElectivesByCourseName(@Param("name")String name);

    @Query("SELECT e FROM Elective e WHERE e.course.id=:id")
    Optional<List<Elective>> getElectivesCourseId(@Param("id")int id);

    @Modifying
    @Query("UPDATE Elective e SET e.score=:score WHERE e.student.id=:studentId AND e.course.id=:courseId")
    int updateGrade(@Param("score")float grade,@Param("studentId")int studentId,@Param("courseId")int courseId);
}
