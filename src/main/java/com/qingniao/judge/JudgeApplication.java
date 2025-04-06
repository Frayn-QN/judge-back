package com.qingniao.judge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JudgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JudgeApplication.class, args);
	}

}
