package com.se2035.jrw.repository;

import com.se2035.jrw.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
}
