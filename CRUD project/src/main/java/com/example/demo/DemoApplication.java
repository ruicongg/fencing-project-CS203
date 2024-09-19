package com.example.demo;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.demo.tournaments.TournamentRepository;

import com.example.demo.tournaments.*;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		// ConfigurableApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);
		// TournamentRepository tournamentRepository = ctx.getBean(TournamentRepository.class);
		// System.out.println(tournamentRepository.save(new TournamentEntity(1, "Tournament 1")));
	}

}
