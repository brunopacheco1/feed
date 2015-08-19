package org.ecad.captacao.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import org.ecad.captacao.persistence.RobotGroup;
import org.ecad.captacao.service.RobotGroupService;
import org.jboss.resteasy.annotations.GZIP;
import org.quartz.SchedulerException;

@Path("robot-group")
@Stateless
public class RobotGroupResource extends AbstractResource {

	@EJB
	private RobotGroupService groupService;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(RobotGroup group) throws GenericException, SchedulerException {
		groupService.add(group);
		
		return Response.status(Status.OK).entity(gson.toJson(group)).build();
	}
	
	@PUT
	@Path("/{id}")
	public Response update(@PathParam("id") Long id, RobotGroup group) throws GenericException, SchedulerException {
		groupService.update(id, group);
		
		return Response.status(Status.OK).entity(gson.toJson(group)).build();
	}
	
	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") Long id) throws GenericException {
		RobotGroup group = groupService.get(id);
		
		return Response.status(Status.OK).entity(gson.toJson(group)).build();
	}
	
	@GET
	@GZIP
	public Response list(@DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("100") @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("order") String order, @DefaultValue("asc") @QueryParam("dir") String dir) throws GenericException {
		Map<String, Object> result = new HashMap<>();
		
		List<RobotGroup> groups = groupService.list(start, limit, order, dir);
		
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
	
	@POST
	@Path("/{id}/run")
	public Response runCrawler(@PathParam("id") Long id) throws GenericException {
		List<Map<String, Object>> msgs = groupService.runCrawler(id);
		
		return Response.status(Status.OK).entity(msgs).build();
	}

	@GET
	@Path("/{id}/status")
	public Response status(@PathParam("id") Long id) throws GenericException {
		List<Map<String, Object>> msgs = groupService.status(id);
		
		return Response.status(Status.OK).entity(msgs).build();
	}
}
