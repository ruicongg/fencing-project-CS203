package org.fencing.demo;

import org.apache.catalina.core.ApplicationContext;
import org.fencing.demo.tournaments.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TournamentApplication {

	public static void main(String[] args) {
		SpringApplication.run(TournamentApplication.class, args);
	}

}
