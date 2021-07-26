package com.chinjja.app.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserCreateDto {
	@Email
	@NotNull
	String email;
	@Size(min = 8)
	@NotNull
	String password;
	String name;
}
