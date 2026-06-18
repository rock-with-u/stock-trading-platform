package com.isyoudwn.execution_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.isyoudwn.execution_service",
		"com.isyoudwn.common_service"
})
public class ExecutionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExecutionServiceApplication.class, args);
	}

}
