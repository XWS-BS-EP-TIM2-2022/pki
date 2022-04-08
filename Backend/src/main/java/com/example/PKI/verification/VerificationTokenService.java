package com.example.PKI.verification;

import org.springframework.beans.factory.annotation.Autowired;

public class VerificationTokenService {
    @Autowired
    VerificationTokenRepository tokenRepository;

    public VerificationToken save(VerificationToken token) { return tokenRepository.save(token); }

    public VerificationToken findByToken(String token) { return tokenRepository.findByToken(token); }
}
