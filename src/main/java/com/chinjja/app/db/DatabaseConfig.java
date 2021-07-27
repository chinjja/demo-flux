package com.chinjja.app.db;

import java.util.Arrays;
import java.util.List;

import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import com.chinjja.app.domain.PostWriteConverter;
import com.chinjja.app.user.User;
import com.chinjja.app.user.UserRepository;
import com.chinjja.app.user.UserRoleWriteConverter;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class DatabaseConfig extends AbstractR2dbcConfiguration {

	@Override
	protected List<Object> getCustomConverters() {
		return Arrays.asList(
				new UserRoleWriteConverter(),
				new PostWriteConverter());
	}

	@Override
	@Bean
	public ConnectionFactory connectionFactory() {
		return new MariadbConnectionFactory(
				MariadbConnectionConfiguration.builder()
				.host("localhost")
				.port(3306)
				.database("demo")
				.username("root")
				.password("a12345678")
				.build());
	}

	@Bean
	public ReactiveAuditorAware<User> reactiveAuditorAware(UserRepository userRepository) {
		return () -> ReactiveSecurityContextHolder.getContext()
				.map(x -> x.getAuthentication())
				.filter(x -> x.isAuthenticated())
				.map(x -> x.getPrincipal())
				.cast(org.springframework.security.core.userdetails.User.class)
				.flatMap(x -> userRepository.findByEmail(x.getUsername()));
	}
}
