package org.example.spring4bookblogproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Spring4BookBlogProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(Spring4BookBlogProjectApplication.class, args);
	}

}
