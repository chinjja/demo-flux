package com.chinjja.app.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import lombok.Value;

@Value
public class UserEmailDto {
	@Email
	@NotNull
	String email;
}
