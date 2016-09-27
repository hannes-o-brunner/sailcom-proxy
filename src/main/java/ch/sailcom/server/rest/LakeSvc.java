package ch.sailcom.server.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.sailcom.server.dto.Lake;
import ch.sailcom.server.proxy.StaticDataProxy;
import ch.sailcom.server.rest.util.Authenticated;
import ch.sailcom.server.rest.util.SvcUtil;

/**
 * Lake Service
 */
@Path("/lakes")
@Authenticated
public class LakeSvc {

	@Inject
	StaticDataProxy staticDataProxy;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Lake> getAllLakes() throws IOException {
		return staticDataProxy.getLakes();
	}

	@GET
	@Path("/{lakeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Lake getLake(@PathParam("lakeId") Integer lakeId) throws IOException {
		if (lakeId == null) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(SvcUtil.getErrorEntity("lakeId parameter is mandatory")).build());
		}
		return staticDataProxy.getLake(lakeId);
	}

}
