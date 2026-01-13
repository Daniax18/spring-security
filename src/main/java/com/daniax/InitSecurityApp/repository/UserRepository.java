package com.daniax.InitSecurityApp.repository;

import com.daniax.InitSecurityApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);
}
