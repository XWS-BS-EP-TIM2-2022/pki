package com.example.PKI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class TestController {
	@GetMapping("/test")
	public ResponseEntity<?> test(){
		return ResponseEntity.ok("OK");
	}
}
