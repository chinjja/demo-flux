package com.chinjja.app.domain;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.r2dbc.core.Parameter;

import lombok.val;

public class PostWriteConverter implements Converter<Post, OutboundRow> {

	@Override
	public OutboundRow convert(Post source) {
		val row = new OutboundRow();
		if(source.getId() != null) {
			row.put("id", Parameter.from(source.getId()));
		}
		row.put("text", Parameter.from(source.getText()));
		row.put("user", Parameter.from(source.getUser().getId()));
		row.put("createdAt", Parameter.from(source.getCreatedAt()));
		return row;
	}

}
