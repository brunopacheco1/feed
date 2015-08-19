package org.ecad.captacao.service;

import java.lang.reflect.Type;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.jboss.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public abstract class AbstractService {

	protected Gson gson;

	protected Logger logger = Logger.getLogger(this.getClass());

	@PostConstruct
	protected void init() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new DateSerializer());
		builder.excludeFieldsWithoutExposeAnnotation();
		gson = builder.create();
	}

	private class DateSerializer implements JsonSerializer<Date> {
		public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.getTime());
		}
	}
}