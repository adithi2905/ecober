package com.ecober;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication(scanBasePackages = "com.ecober")
@EnableCaching
public class EcoberApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
        System.setProperty("GMAP_API_KEY", dotenv.get("GMAP_API_KEY"));
		System.setProperty("OPENROUTER_API_KEY", dotenv.get("OPENROUTER_API_KEY"));
		System.setProperty("CLIMATIQ_API_KEY", dotenv.get("CLIMATIQ_API_KEY"));
		
		SpringApplication.run(EcoberApplication.class, args);
	}
}
