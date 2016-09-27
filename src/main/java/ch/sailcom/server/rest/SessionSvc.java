package ch.sailcom.server.rest;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.sailcom.server.dto.SessionInfo;
import ch.sailcom.server.proxy.SessionProxy;
import ch.sailcom.server.proxy.impl.NoSessionException;
import ch.sailcom.server.rest.util.Authenticated;
import ch.sailcom.server.rest.util.SvcUtil;

/**
 * Sailcom SessionProxy Service
 */
@Path("/session")
public class SessionSvc {

	private static Logger LOGGER = LoggerFactory.getLogger(SessionSvc.class);

	@Context
	HttpServletRequest request;

	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public SessionInfo login(@QueryParam("user") String user, @QueryParam("pwd") String pwd) throws IOException {

		try {
			if (user == null) {
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(SvcUtil.getErrorEntity("user parameter is mandatory")).build());
			} else if (pwd == null) {
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(SvcUtil.getErrorEntity("pwd parameter is mandatory")).build());
			}

			SessionProxy session = SvcUtil.getSessionProxy(request);
			if (session == null) {
				LOGGER.debug("login.initSessionProxy {}", user);
				session = SvcUtil.initSessionProxy(request);
			}

			if (session.isLoggedIn()) {
				return new SessionInfo(session.getSessionId(), session.getUser());
			}

			LOGGER.info("User {} logging in ...", user);
			if (!session.login(user, pwd)) {
				LOGGER.info("User {} login failed", user);
				throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).entity(SvcUtil.getErrorEntity("login denied")).build());
			}

			LOGGER.info("User {} successfully logged in", user);
			return new SessionInfo(session.getSessionId(), session.getUser());

		} catch (Exception e) {
			LOGGER.error("login crashed", e);
			throw e;
		}

	}

	@GET
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public void logout() throws IOException {

		SessionProxy session = SvcUtil.getSessionProxy(request);

		try {
			if (session.isLoggedIn()) {
				session.logout();
			}
		} catch (NoSessionException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(SvcUtil.getErrorEntity("logout failed")).build());
		}

	}

}
