package com.bankapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title ="Alpha Bank",
				description = "Backend Rest APIs for Alpha Bank",
				version = "v1.0",
				contact = @Contact(
						name = "sunday peter",
						email = "sundaypetersp12@gmail.com",
						url = "https://github.com/code-with-sunday/Alpha_bank.git"
				),
				license = @License(
						name = "Alpha Bank",
						url = "https://github.com/code-with-sunday/Alpha_bank.git"
				)

		),

		externalDocs = @ExternalDocumentation(
				description = "Backend Rest APIs for Alpha Bank",
				url = "https://github.com/code-with-sunday/Alpha_bank.git"
		)
)
public class BankappApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankappApplication.class, args);
	}

}
