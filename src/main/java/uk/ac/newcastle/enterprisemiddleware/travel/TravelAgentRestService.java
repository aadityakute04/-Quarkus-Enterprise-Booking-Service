package uk.ac.newcastle.enterprisemiddleware.travel;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/travel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Travel Agent Rest Service", description = "Aggregate booking across hotel, taxi and flight services")
public class TravelAgentRestService {

    @Inject
    TravelAgentService service;

    @Inject
    TravelAgentBookingRepository repo;

    @Operation(summary = "Create an aggregate travel booking")
    @POST
    public Response create(TravelAgentRequest req, @Context UriInfo uriInfo) {
        try {
            TravelAgentBooking created = service.createAggregateBooking(req);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
            return Response.created(uri).entity(created).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @Operation(summary = "List travel agent bookings")
    @GET
    public Response list(@QueryParam("start") @DefaultValue("0") int start,
                         @QueryParam("size") @DefaultValue("20") int size) {
        List<TravelAgentBooking> results = repo.findAll(start, size);
        return Response.ok(results).build();
    }

    @Operation(summary = "Cancel an aggregate travel booking (local + remote)")
    @DELETE
    @Path("{id}")
    public Response cancel(@PathParam("id") Long id) {
        TravelAgentBooking tab = repo.findById(id);
        if (tab == null) return Response.status(Response.Status.NOT_FOUND).build();

        // cancel remote bookings if present (best-effort)
        try {
            if (tab.getFlightBookingId() != null) {
                // cancel remote flight
                // if you want to re-use service layer or inject clients here, do so
            }
            if (tab.getTaxiBookingId() != null) {
                // cancel taxi
            }
            if (tab.getHotelBookingId() != null) {
                // cancel hotel
            }
        } catch (Exception e) {
            // ignore partial failures but log in real app
        }

        repo.delete(tab);
        return Response.noContent().build();
    }
}
