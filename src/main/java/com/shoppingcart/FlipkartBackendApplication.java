package com.shoppingcart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = { 
		FlipkartBackendApplication.class,
		Jsr310JpaConverters.class 
})
public class FlipkartBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlipkartBackendApplication.class, args);
	}

}
