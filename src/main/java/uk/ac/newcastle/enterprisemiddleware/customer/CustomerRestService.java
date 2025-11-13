package uk.ac.newcastle.enterprisemiddleware.customer;

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

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer Rest Service", description = "Operations related to Customer resource")
public class CustomerRestService {

    @Inject
    CustomerService service;

    @Operation(summary = "List all customers", description = "Returns a list of customers (paginated)")
    @APIResponse(responseCode = "200", description = "Customers retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Customer.class)))
    @GET
    public Response list(@QueryParam("start") @DefaultValue("0") int start,
                         @QueryParam("size") @DefaultValue("20") int size) {
        List<Customer> list = service.findAll(start, size);
        return Response.ok(list).build();
    }

    @Operation(summary = "Find customer by ID")
    @APIResponse(responseCode = "200", description = "Customer found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)))
    @APIResponse(responseCode = "404", description = "Customer not found")
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") Long id) {
        Optional<Customer> customer = service.findById(id);
        return customer.map(Response::ok)
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @Operation(summary = "Create a new customer")
    @APIResponse(responseCode = "201", description = "Customer created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)))
    @APIResponse(responseCode = "400", description = "Invalid customer data")
    @APIResponse(responseCode = "409", description = "Email already exists")
    @POST
    public Response create(Customer customer, @Context UriInfo uriInfo) {
        try {
            Customer created = service.create(customer);
            URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
            return Response.created(uri).entity(created).build();
        } catch (UniqueEmailException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Unexpected error: " + e.getMessage()).build();
        }
    }

    @Operation(summary = "Update an existing customer")
    @APIResponse(responseCode = "200", description = "Customer updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)))
    @APIResponse(responseCode = "404", description = "Customer not found")
    @APIResponse(responseCode = "409", description = "Email already exists")
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, Customer customer) {
        try {
            if (customer.getId() == null) {
                customer.setId(id);
            }
            if (service.findById(id).isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            Customer updated = service.update(customer);
            return Response.ok(updated).build();
        } catch (UniqueEmailException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (ConstraintViolationException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().entity("Unexpected error: " + e.getMessage()).build();
        }
    }

    @Operation(summary = "Delete a customer")
    @APIResponse(responseCode = "204", description = "Customer deleted")
    @APIResponse(responseCode = "404", description = "Customer not found")
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
