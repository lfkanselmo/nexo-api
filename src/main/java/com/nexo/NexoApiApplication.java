package com.nexo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NexoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexoApiApplication.class, args);
	}

}
