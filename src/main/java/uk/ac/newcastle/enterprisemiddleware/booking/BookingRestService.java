package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Booking Rest Service", description = "Operations related to booking resource")
public class BookingRestService {

    @Inject
    BookingService service;

    @Operation(summary = "List bookings")
    @APIResponse(responseCode = "200", description = "Bookings retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class)))
    @GET
    public Response list(@QueryParam("start") @DefaultValue("0") int start,
                         @QueryParam("size") @DefaultValue("20") int size) {
        List<Booking> list = service.findAll(start, size);
        return Response.ok(list).build();
    }

    @Operation(summary = "Get a booking by id")
    @APIResponse(responseCode = "200", description = "Booking found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class)))
    @APIResponse(responseCode = "404", description = "Booking not found")
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        Optional<Booking> b = service.findById(id);
        return b.map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @Operation(summary = "Create a new booking")
    @APIResponse(responseCode = "201", description = "Booking created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class)))
    @APIResponse(responseCode = "400", description = "Invalid booking data")
    @APIResponse(responseCode = "409", description = "Booking reference conflict")
    @POST
    public Response create(Booking booking, @Context UriInfo uriInfo) {
        try {
            Booking created = service.create(booking);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
            return Response.created(uri).entity(created).build();
        } catch (UniqueBookingReferenceException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Unexpected error: " + e.getMessage()).build();
        }
    }

    @Operation(summary = "Update a booking")
    @APIResponse(responseCode = "200", description = "Booking updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Booking.class)))
    @APIResponse(responseCode = "404", description = "Booking not found")
    @APIResponse(responseCode = "409", description = "Booking reference conflict")
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Booking booking) {
        try {
            if (booking.getId() == null) {
                booking.setId(id);
            }
            if (service.findById(id).isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            Booking updated = service.update(booking);
            return Response.ok(updated).build();
        } catch (UniqueBookingReferenceException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Unexpected error: " + e.getMessage()).build();
        }
    }

    @Operation(summary = "Cancel (delete) a booking")
    @APIResponse(responseCode = "204", description = "Booking deleted")
    @APIResponse(responseCode = "404", description = "Booking not found")
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        if (service.findById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        service.delete(id);
        return Response.noContent().build();
    }
}
