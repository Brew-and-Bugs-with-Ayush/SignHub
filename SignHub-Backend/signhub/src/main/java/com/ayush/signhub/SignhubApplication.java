package com.ayush.signhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class SignhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignhubApplication.class, args);
	}

}
