package com.chinjja.app.db;

import java.util.Arrays;
import java.util.List;

import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import com.chinjja.app.user.UserRoleWriteConverter;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableR2dbcRepositories
public class DatabaseConfig extends AbstractR2dbcConfiguration {

	@Override
	protected List<Object> getCustomConverters() {
		return Arrays.asList(
				new UserRoleWriteConverter());
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

}
