package com.test.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CryptoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoApplication.class, args);
	}

}
