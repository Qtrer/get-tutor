package com.example.springbootjpa.repository;

import com.example.springbootjpa.entity.Elective;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectiveRepository extends BaseRepository<Elective, Integer>{
    @Query("FROM Elective e")
    Optional<List<Elective>> list();
}
