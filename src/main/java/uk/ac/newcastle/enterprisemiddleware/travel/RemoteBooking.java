package uk.ac.newcastle.enterprisemiddleware.travel;

/**
 * Minimal DTO for remote booking responses.
 * Assumes remote services return at least an "id" field.
 */
public class RemoteBooking {
    public Long id;
    public String bookingReference;

}
