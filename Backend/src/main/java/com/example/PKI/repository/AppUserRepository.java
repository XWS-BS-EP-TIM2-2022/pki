package com.example.PKI.repository;

import com.example.PKI.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @Query("select u from AppUser u where u.email = ?1")
    public AppUser findByEmail(String email);
}
