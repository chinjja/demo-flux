package com.chinjja.app.domain;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import com.chinjja.app.user.User;

import lombok.Builder;
import lombok.With;
import lombok.Value;

@Value
@Builder
@With
public class Post {
	@Id
	Long id;
	
	@NotBlank
	@NotNull
	String text;
	
	@CreatedBy
	User user;
	
	@CreatedDate
	LocalDateTime createdAt;
}
