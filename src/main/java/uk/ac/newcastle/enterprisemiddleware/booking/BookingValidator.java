package uk.ac.newcastle.enterprisemiddleware.booking;

import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerRepository;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;
import java.util.Optional;

@ApplicationScoped
public class BookingValidator {

    @Inject
    Validator validator;

    @Inject
    BookingRepository bookingRepository;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    HotelRepository hotelRepository;


    public void validateBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking was null.");
        }

        // Bean validation
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Booking validation failed", violations);
        }

        // startDate <= endDate
        LocalDate s = booking.getStartDate();
        LocalDate e = booking.getEndDate();
        if (s != null && e != null && s.isAfter(e)) {
            throw new IllegalArgumentException("startDate must be on or before endDate.");
        }

        // bookingReference uniqueness (for create/update)
        if (booking.getBookingReference() != null) {
            Optional<Booking> existing = bookingRepository.findByReference(booking.getBookingReference());
            if (existing.isPresent() && (booking.getId() == null || !existing.get().getId().equals(booking.getId()))) {
                throw new RuntimeException("Booking reference already exists: " + booking.getBookingReference());
            }
        }

        // referenced customer exists
        Customer c = booking.getCustomer();
        if (c == null || c.getId() == null || customerRepository.findById(c.getId()).isEmpty()) {
            throw new IllegalArgumentException("Referenced customer must exist (provide valid customer id).");
        }

        // referenced hotel exists
        Hotel h = booking.getHotel();
        if (h == null || h.getId() == null || hotelRepository.findById(h.getId()).isEmpty()) {
            throw new IllegalArgumentException("Referenced hotel must exist (provide valid hotel id).");
        }
    }
}
