package uk.ac.newcastle.enterprisemiddleware.hotel;

public class UniqueHotelNameException extends Exception {

    public UniqueHotelNameException() { super(); }

    public UniqueHotelNameException(String message) { super(message); }

    public UniqueHotelNameException(String message, Throwable cause) { super(message, cause); }
}
