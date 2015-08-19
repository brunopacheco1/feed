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
import org.ecad.captacao.persistence.AppUserGroup;
import org.ecad.captacao.service.AppUserGroupService;
import org.jboss.resteasy.annotations.GZIP;

@Path("user-group")
@Stateless
public class AppUserGroupResource extends AbstractResource {

	@Inject
	private AppUserGroupService groupService;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(AppUserGroup group) throws GenericException {
		groupService.add(group);
		
		return Response.status(Status.OK).entity(gson.toJson(group)).build();
	}
	
	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") Long id, AppUserGroup group) throws GenericException {
		groupService.update(id, group);
		
		return Response.status(Status.OK).entity(gson.toJson(group)).build();
	}
	
	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") Long id) throws GenericException {
		AppUserGroup group = groupService.get(id);
		
		return Response.status(Status.OK).entity(gson.toJson(group)).build();
	}
	
	@GET
	@GZIP
	public Response list(@DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("100") @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("order") String order, @DefaultValue("asc") @QueryParam("dir") String dir) throws GenericException {
		Map<String, Object> result = new HashMap<>();
		
		List<AppUserGroup> groups = groupService.list(start, limit, order, dir);
		
		result.put("resultSize", groups.size());
		result.put("totalSize", groupService.getCount());
		result.put("result", groups);
		
		return Response.status(Status.OK).entity(gson.toJson(result)).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response remove(@PathParam("id") Long id) throws GenericException {
		groupService.remove(id);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("removed", true);
		
		return Response.status(Status.OK).entity(response).build();
	}
}