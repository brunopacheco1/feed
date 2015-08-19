package org.ecad.captacao.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ecad.captacao.exception.GenericException;
import org.ecad.captacao.persistence.Monitoring;
import org.ecad.captacao.service.MonitoringService;
import org.jboss.resteasy.annotations.GZIP;

@Path("monitoring")
@Stateless
public class MonitoringResource extends AbstractResource {

	@EJB
	private MonitoringService monitoringService;
	
	@GET
	@Path("/{id}")
	public Response get(@PathParam("id") Long id) throws GenericException {
		Monitoring monitoring = monitoringService.get(id);
		
		return Response.status(Status.OK).entity(gson.toJson(monitoring)).build();
	}
	
	@GET
	@GZIP
	public Response list(@DefaultValue("0") @QueryParam("start") Integer start, @DefaultValue("100") @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("order") String order, @DefaultValue("asc") @QueryParam("dir") String dir) throws GenericException {
		Map<String, Object> result = new HashMap<>();
		
		List<Monitoring> monitorings = monitoringService.list(start, limit, order, dir);
		
		result.put("resultSize", monitorings.size());
		result.put("totalSize", monitoringService.getCount());
		result.put("result", monitorings);
		
		return Response.status(Status.OK).entity(gson.toJson(result)).build();
	}
}