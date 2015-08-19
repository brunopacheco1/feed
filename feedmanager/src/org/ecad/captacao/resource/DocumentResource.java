package org.ecad.captacao.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.persistence.Document;
import org.ecad.captacao.persistence.NormalizationStatus;
import org.ecad.captacao.service.DocumentService;

@Path("document")
@Stateless
public class DocumentResource extends AbstractResource {

	@Inject
	private DocumentService service;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Document document) throws GenericException {
		service.add(document);
		
		return Response.status(Status.OK).entity(gson.toJson(document)).build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") Long id, Document document) throws GenericException {
		service.update(id, document);
		
		return Response.status(Status.OK).entity(gson.toJson(document)).build();
	}
	
	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") Long id) throws GenericException {
		Document document = service.get(id);
		
		return Response.status(Status.OK).entity(gson.toJson(document)).build();
	}
	
	@GET
	public Response list(@DefaultValue("NOT_NORMALIZED") @QueryParam("status") NormalizationStatus status, @DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("100") @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("order") String order, @DefaultValue("asc") @QueryParam("dir") String dir) throws GenericException {
		Map<String, Object> result = new HashMap<>();
		
		List<Document> documents = service.list(status, start, limit, order, dir);
		
		result.put("resultSize", documents.size());
		result.put("totalSize", service.getCount(status));
		result.put("result", documents);
		
		return Response.status(Status.OK).entity(gson.toJson(result)).build();
	}
	
	
	@DELETE
	@Path("/{id}")
	public Response remove(@PathParam("id") Long id) throws GenericException {
		service.remove(id);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("removed", true);
		
		return Response.status(Status.OK).entity(response).build();
	}
}
