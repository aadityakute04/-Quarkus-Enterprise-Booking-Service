package uk.ac.newcastle.enterprisemiddleware.travel;

/**
 * Minimal DTO for remote booking responses.
 * Assumes remote services return at least an "id" field.
 */
public class RemoteBooking {
    public Long id;
    public String bookingReference;

    // add getters/setters if you prefer; public fields work with Jackson by default
}
