package com.nl.nlstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class NlstudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(NlstudyApplication.class, args);
	}

}
