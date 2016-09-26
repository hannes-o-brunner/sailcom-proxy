package ch.sailcom.server.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.sailcom.server.dto.Booking;
import ch.sailcom.server.dto.StaticData;
import ch.sailcom.server.proxy.BookingProxy;
import ch.sailcom.server.proxy.StaticDataProxy;

/**
 * Bookings Service
 */
@Path("/bookings")
public class BookingSvc {

	private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Booking> getBookings(@Context HttpServletRequest request, @QueryParam("shipId") Integer shipId, @QueryParam("date") String date, @QueryParam("nofWeeks") Integer nofWeeks)
			throws IOException {

		StaticData sd = SvcUtil.getSessionProxy(request).getProxy(StaticDataProxy.class).getStaticData();
		BookingProxy bookingProxy = SvcUtil.getSessionProxy(request).getProxy(BookingProxy.class);

		/* Validate Service Input */
		if (shipId == null) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(SvcUtil.getErrorMessage("shipId parameter is mandatory")).build());
		} else if (sd.getShip(shipId) == null) {
			throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_NOT_FOUND).entity(SvcUtil.getErrorMessage("ship not found")).build());
		}

		nofWeeks = nofWeeks == null ? 1 : nofWeeks;
		Date currDate = null;
		if (date != null && !date.equals("")) {
			try {
				currDate = df.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			currDate = cal.getTime();
		}

		return bookingProxy.getBookings(shipId, currDate, nofWeeks);

	}

}
