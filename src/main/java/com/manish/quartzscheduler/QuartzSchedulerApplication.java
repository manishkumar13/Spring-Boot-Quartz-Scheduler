package com.manish.quartzscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableScheduling
public class QuartzSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuartzSchedulerApplication.class, args);
	}

}
