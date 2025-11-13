package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.validation.ConstraintViolationException;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/hotels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Hotel Rest Service", description = "Operations related to hotel resource")
public class HotelRestService {

    @Inject
    HotelService service;

    @Operation(summary = "List hotels")
    @APIResponse(responseCode = "200", description = "Hotels retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Hotel.class)))
    @GET
    public Response list(@QueryParam("start") @DefaultValue("0") int start,
                         @QueryParam("size") @DefaultValue("20") int size) {
        List<Hotel> list = service.findAll(start, size);
        return Response.ok(list).build();
    }

    @Operation(summary = "Get a hotel by id")
    @APIResponse(responseCode = "200", description = "Hotel found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Hotel.class)))
    @APIResponse(responseCode = "404", description = "Hotel not found")
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        Optional<Hotel> hotel = service.findById(id);
        return hotel.map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @Operation(summary = "Create a new hotel")
    @APIResponse(responseCode = "201", description = "Hotel created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Hotel.class)))
    @APIResponse(responseCode = "400", description = "Invalid hotel data")
    @APIResponse(responseCode = "409", description = "Hotel name conflict")
    @POST
    public Response create(Hotel hotel, @Context UriInfo uriInfo) {
        try {
            Hotel created = service.create(hotel);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
            return Response.created(uri).entity(created).build();
        } catch (UniqueHotelNameException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Unexpected error: " + e.getMessage()).build();
        }
    }

    @Operation(summary = "Update an existing hotel")
    @APIResponse(responseCode = "200", description = "Hotel updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Hotel.class)))
    @APIResponse(responseCode = "404", description = "Hotel not found")
    @APIResponse(responseCode = "409", description = "Hotel name conflict")
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Hotel hotel) {
        try {
            if (hotel.getId() == null) {
                hotel.setId(id);
            }
            if (service.findById(id).isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            Hotel updated = service.update(hotel);
            return Response.ok(updated).build();
        } catch (UniqueHotelNameException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Unexpected error: " + e.getMessage()).build();
        }
    }

    @Operation(summary = "Delete hotel")
    @APIResponse(responseCode = "204", description = "Hotel deleted")
    @APIResponse(responseCode = "404", description = "Hotel not found")
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
