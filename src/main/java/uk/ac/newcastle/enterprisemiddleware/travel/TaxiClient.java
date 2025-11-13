package uk.ac.newcastle.enterprisemiddleware.travel;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/taxis")
@RegisterRestClient(configKey = "taxi-api")
public interface TaxiClient {

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
        public Long taxiId;
        public String bookingReference;
        public String startDate;
        public String endDate;
        public Double totalPrice;
    }
}
