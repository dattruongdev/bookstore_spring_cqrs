package com.dattruongdev.bookstore_cqrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class BookstoreCqrsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookstoreCqrsApplication.class, args);
	}

}
