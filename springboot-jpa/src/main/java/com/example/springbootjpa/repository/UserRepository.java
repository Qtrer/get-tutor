package com.example.springbootjpa.repository;

import com.example.springbootjpa.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Integer>{
    @Modifying
    @Query("UPDATE User u SET u.password=:password WHERE u.id=:id")
    int updatePassword(@Param("password") String password, @Param("id") int id);

    Optional<User> findByNumber(int number);
    Optional<User> findByName(String name);
}
