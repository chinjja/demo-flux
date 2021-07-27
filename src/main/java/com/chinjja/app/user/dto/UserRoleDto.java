package com.chinjja.app.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserRoleDto {
	@Email
	@NotNull
	String email;

	@NotBlank
	@NotNull
	String role;
}
