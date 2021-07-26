package com.chinjja.app.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

import lombok.val;

public class UserRoleWriteConverter implements Converter<UserRole, OutboundRow> {

	@Override
	public OutboundRow convert(UserRole source) {
		val row = new OutboundRow();
		if(source.getId() != null) {
			row.put("id", Parameter.from(source.getId()));
		}
		row.put("user", Parameter.from(source.getUser().getId()));
		row.put("role", Parameter.from(source.getRole()));
		return row;
	}

}
