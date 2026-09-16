package com.backend.gapfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// Sin esto, los metodos @Scheduled no se ejecutan nunca: es lo que dejaba
// las Open Tables vencidas en ACTIVE para siempre.
@EnableScheduling
public class GapfinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GapfinderApplication.class, args);
	}

}
