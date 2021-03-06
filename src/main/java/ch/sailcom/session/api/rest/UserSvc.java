package ch.sailcom.session.api.rest;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.sailcom.session.domain.User;
import ch.sailcom.session.domain.UserInfo;
import ch.sailcom.session.service.UserService;

/**
 * User Service
 */
@Path("/user")
@Authenticated
public class UserSvc {

	@Inject
	UserService userService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser() throws IOException {
		return userService.getUser();
	}

	@GET
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfo getUserInfo() throws IOException {
		return userService.getUserInfo();
	}

	@GET
	@Path("/availableLakes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getLakes() throws IOException {
		return userService.getAvailableLakes();
	}

	@GET
	@Path("/availableHarbors")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getHarbors() throws IOException {
		return userService.getAvailableHarbors();
	}

	@GET
	@Path("/availableShips")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getShips() throws IOException {
		return userService.getAvailableShips();
	}

}
