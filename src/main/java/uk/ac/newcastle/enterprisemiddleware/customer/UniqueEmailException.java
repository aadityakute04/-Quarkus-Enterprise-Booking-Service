package uk.ac.newcastle.enterprisemiddleware.customer;

public class UniqueEmailException extends Exception {

    public UniqueEmailException() { super(); }

    public UniqueEmailException(String message) { super(message); }

    public UniqueEmailException(String message, Throwable cause) { super(message, cause); }
}
