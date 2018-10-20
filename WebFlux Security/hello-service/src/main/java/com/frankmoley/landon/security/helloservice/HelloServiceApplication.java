package com.frankmoley.landon.security.helloservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@EnableWebFluxSecurity
public class HelloServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloServiceApplication.class, args);
	}
	
	
	/*
	 * Add authorize user details to access our API's
	 */
	@Bean
	public MapReactiveUserDetailsService userDetailsService() {
		List<UserDetails> userDetails = new ArrayList<>();
		userDetails.add(User.withDefaultPasswordEncoder()
				.username("prateek")
				.password("password")
				.roles("USER","ADMIN")
				.build()
				);
		
		userDetails.add(User.withDefaultPasswordEncoder()
				.username("joshi")
				.password("password")
				.roles("USER")
				.build()
				);		
		return new MapReactiveUserDetailsService(userDetails);
	}
	
	/*
	 * We want anybody can access uri /hello
	 * but we want only authorize user with role ADMIN to access any other uri for our app (like /roll)
	 */
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http)
	{
		http.authorizeExchange().pathMatchers("/hello").permitAll()
			.anyExchange().hasRole("ADMIN")
			.and().httpBasic();								//httpBasic: username and password
		return http.build();
	}
}
