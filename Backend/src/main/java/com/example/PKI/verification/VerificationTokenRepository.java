package com.example.PKI.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @Query("select t from VerificationToken t where t.token = ?1")
    public VerificationToken findByToken(String token);
}
