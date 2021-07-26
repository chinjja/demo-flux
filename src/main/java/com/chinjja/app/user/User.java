package com.chinjja.app.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class User {
	@Id
	Long id;
	@Email
	@NotNull
	String email;
	
	@JsonIgnore
	@NotNull
	String password;
	String name;
}
