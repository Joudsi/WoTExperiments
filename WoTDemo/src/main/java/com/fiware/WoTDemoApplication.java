package com.fiware;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fiware.services.WoTService;

@SpringBootApplication
public class WoTDemoApplication  {
	
	public static void main(String[] args) {
		SpringApplication.run(WoTDemoApplication.class, args);
	}
	
}
