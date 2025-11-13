package uk.ac.newcastle.enterprisemiddleware.booking;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;

import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Path("/guestbookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {

    @Inject
    CustomerService customerService;

    @Inject
    BookingService bookingService;

    // Inject UserTransaction via CDI (Quarkus + Narayana provide this)
    @Inject
    UserTransaction userTransaction;

    @POST
    public Response createGuestBooking(GuestBooking guestBooking, @Context UriInfo uriInfo) {
        if (guestBooking == null || guestBooking.getCustomer() == null || guestBooking.getBooking() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("GuestBooking must contain customer and booking").build();
        }

        try {
            // begin manual JTA transaction
            userTransaction.begin();

            // create customer first (validation happens inside service)
            Customer createdCustomer = customerService.create(guestBooking.getCustomer());

            // attach created customer to booking and persist booking
            Booking bookingToCreate = guestBooking.getBooking();
            bookingToCreate.setCustomer(createdCustomer);

            Booking createdBooking = bookingService.create(bookingToCreate);

            // commit transaction
            userTransaction.commit();

            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(createdBooking.getId())).build();
            return Response.created(uri).entity(createdBooking).build();

        } catch (UniqueBookingReferenceException e) {
            // domain conflict -> rollback
            safeRollback();
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            safeRollback();
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            safeRollback();
            return Response.serverError().entity("Failed to create guest booking: " + e.getMessage()).build();
        }
    }

    // helper to rollback without throwing on rollback failure
    private void safeRollback() {
        try {
            if (userTransaction != null) {
                userTransaction.rollback();
            }
        } catch (Exception ex) {
            // log if you have a logger; ignore here to avoid masking original exception
        }
    }
}
