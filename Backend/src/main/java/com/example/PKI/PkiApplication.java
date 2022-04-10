package com.example.PKI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@org.springframework.data.jpa.repository.config.EnableJpaRepositories
@SpringBootApplication
public class PkiApplication {
	public static void main(String[] args) {
		SpringApplication.run(PkiApplication.class, args);
	}
}
