package com.shop.akisshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.shop.akisshop.config.RsaConfigProperties;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableConfigurationProperties(RsaConfigProperties.class)
public class AkisshopApplication {
	static {
        Dotenv dotenv = Dotenv.configure()
		.directory("./") // Pastikan membaca dari root
		.ignoreIfMissing()
		.load();
	dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
	}
	public static void main(String[] args) {
		System.out.println("DB_URL from System Properties: " + System.getProperty("DB_URL"));
		SpringApplication.run(AkisshopApplication.class, args);
	}

}
