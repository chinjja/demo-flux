package com.chinjja.app.user.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Value;

@Value
public class UserPasswordDto {
	@Size(min = 8)
	@NotNull
	String password;
}
