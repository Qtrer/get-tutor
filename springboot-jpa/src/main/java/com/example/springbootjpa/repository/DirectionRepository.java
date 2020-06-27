package com.example.springbootjpa.repository;

import com.example.springbootjpa.entity.Direction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectionRepository extends BaseRepository<Direction, Integer>{
    @Query("SELECT d FROM Direction d WHERE d.student.id=:id")
    Optional<List<Direction>> getDirectionByStudentId (@Param("id")int id);

    Optional<Direction> findById(int id);
    Optional<Direction> findByName(String name);
    void deleteDirectionByName(String name);
}
