package com.permitseoul.permitserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class PermitServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PermitServerApplication.class, args);
	}

}
