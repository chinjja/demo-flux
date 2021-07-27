package com.chinjja.app.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.chinjja.app.user.UserRepository;
import com.chinjja.app.user.UserRoleRepository;
import com.chinjja.app.user.UserService;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
		.authorizeExchange()
		.anyExchange().permitAll()
		.and()
		.httpBasic().and()
		.formLogin()
		.and()
		.csrf().disable();
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public ReactiveUserDetailsService reactiveUserDetailsService(
			UserRepository userRepository,
			UserRoleRepository userRoleRepository) {
		return username -> userRepository.findByEmail(username)
				.flatMap(user -> userRoleRepository.findAllRoleByUser(user)
						.collectList()
						.map(roles -> User.builder()
								.username(user.getEmail())
								.password(user.getPassword())
								.roles(roles.toArray(new String[0]))
								.build()));
	}
	
	@Bean
	public CommandLineRunner initUser(UserService userService) {
		return args -> userService.init().subscribe();
	}
}
