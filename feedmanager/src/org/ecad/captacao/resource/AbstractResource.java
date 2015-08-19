package org.ecad.captacao.resource;

import java.lang.reflect.Type;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.interceptor.Interceptors;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ecad.captacao.interceptor.RequestTimeInterceptor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Produces(MediaType.APPLICATION_JSON)
@Interceptors(RequestTimeInterceptor.class)
public abstract class AbstractResource {

	protected Gson gson;

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