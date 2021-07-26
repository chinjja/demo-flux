package com.chinjja.app.user;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class UserRole {
	@Id
	Long id;
	
	@NotNull
	User user;
	
	@NotNull
	String role;
}
