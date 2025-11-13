package uk.ac.newcastle.enterprisemiddleware.booking;

public class UniqueBookingReferenceException extends Exception {

    public UniqueBookingReferenceException() { super(); }
    public UniqueBookingReferenceException(String message) { super(message); }
    public UniqueBookingReferenceException(String message, Throwable cause) { super(message, cause); }
}
