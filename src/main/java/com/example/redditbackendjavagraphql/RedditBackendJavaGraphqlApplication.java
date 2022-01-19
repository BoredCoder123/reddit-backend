package com.example.redditbackendjavagraphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"com.example.redditbackendjavagraphql.query"})
@SpringBootApplication
public class RedditBackendJavaGraphqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditBackendJavaGraphqlApplication.class, args);
	}

}
