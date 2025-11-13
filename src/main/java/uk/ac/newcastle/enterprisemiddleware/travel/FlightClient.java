package uk.ac.newcastle.enterprisemiddleware.travel;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/flights")
@RegisterRestClient(configKey = "flight-api")
public interface FlightClient {

    @POST
    @Path("/bookings")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    RemoteBooking createBooking(RemoteBookingRequest req);

    @DELETE
    @Path("/bookings/{id}")
    void cancelBooking(@PathParam("id") Long id);

    class RemoteBookingRequest {
        public Long customerId;
        public Long flightId;
        public String bookingReference;
        public String startDate;
        public String endDate;
        public Double totalPrice;
    }
}
