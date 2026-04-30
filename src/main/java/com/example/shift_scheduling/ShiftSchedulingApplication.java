package com.example.shift_scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShiftSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShiftSchedulingApplication.class, args);
	}

}
