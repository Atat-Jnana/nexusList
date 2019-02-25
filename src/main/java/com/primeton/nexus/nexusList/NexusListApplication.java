package com.primeton.nexus.nexusList;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class NexusListApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(NexusListApplication.class, args);
	}

}
