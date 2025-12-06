package uk.ac.newcastle.enterprisemiddleware.booking;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerRepository;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.net.URI;
import java.util.Optional;


// Create bookings using BookingDTO (customerId + hotelId).

@Path("/bookingdto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "BookingDTO", description = "Create bookings by providing customerId and hotelId")
public class BookingDTOService {

    @Inject
    BookingService bookingService;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    HotelRepository hotelRepository;

    @Operation(summary = "Create booking using customerId and hotelId")
    @APIResponse(responseCode = "201", description = "Booking created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class)))
    @APIResponse(responseCode = "400", description = "Invalid input or referenced entity not found")
    @APIResponse(responseCode = "409", description = "Booking reference conflict")
    @POST
    @Transactional
    public Response createBookingFromDTO(BookingDTO dto, @Context UriInfo uriInfo) {
        if (dto == null || dto.getCustomerId() == null || dto.getHotelId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("customerId and hotelId are required.")
                    .build();
        }

        // fetch customer (repository returns Optional)
        Optional<Customer> maybeCustomer = customerRepository.findById(dto.getCustomerId());
        if (maybeCustomer.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Customer not found with ID: " + dto.getCustomerId())
                    .build();
        }
        Customer customer = maybeCustomer.get();

        // fetch hotel (repository returns Optional)
        Optional<Hotel> maybeHotel = hotelRepository.findById(dto.getHotelId());
        if (maybeHotel.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Hotel not found with ID: " + dto.getHotelId())
                    .build();
        }
        Hotel hotel = maybeHotel.get();

        // Map DTO -> Booking entity
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setHotel(hotel);
        booking.setBookingReference(dto.getBookingReference());
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStatus(dto.getStatus());
        booking.setTotalPrice(dto.getTotalPrice());

        try {
            // Persist via service (validator will run)
            Booking created = bookingService.create(booking);

            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
            return Response.created(uri).entity(created).build();

        } catch (UniqueBookingReferenceException e) {
            // Booking reference conflict -> 409 Conflict
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (IllegalArgumentException | javax.validation.ConstraintViolationException e) {
            // Validation errors -> 400 Bad Request
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            // Unexpected error -> 500
            return Response.serverError().entity("Unexpected error: " + e.getMessage()).build();
        }
    }
}
