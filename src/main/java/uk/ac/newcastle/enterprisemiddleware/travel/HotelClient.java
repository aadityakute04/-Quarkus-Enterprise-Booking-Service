package uk.ac.newcastle.enterprisemiddleware.travel;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/hotels")
@RegisterRestClient(configKey = "hotel-api")
public interface HotelClient {

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
        public Long hotelId;
        public String bookingReference;
        public String startDate;
        public String endDate;
        public Double totalPrice;
    }
}
