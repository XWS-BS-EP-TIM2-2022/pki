package com.example.PKI.repository;

import com.example.PKI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = ?1")
    public User findByEmail(String email);

    @Query("select u from User u where u.username = ?1")
    public User getByUsername(String admin);
}
