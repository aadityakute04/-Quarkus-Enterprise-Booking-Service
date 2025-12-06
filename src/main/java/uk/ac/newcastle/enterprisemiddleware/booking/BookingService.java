package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class BookingService {

    private static final Logger log = Logger.getLogger(BookingService.class);

    @Inject
    BookingRepository repository;

    @Inject
    BookingValidator validator;

    public Optional<Booking> findById(Long id) {
        return repository.findById(id);
    }

    public List<Booking> findAll(int start, int max) {
        return repository.findAll(start, max);
    }

    @Transactional
    public Booking create(Booking booking) throws UniqueBookingReferenceException {
        validator.validateBooking(booking);

        // check unique bookingReference
        if (booking.getBookingReference() != null && repository.findByReference(booking.getBookingReference()).isPresent()) {
            throw new UniqueBookingReferenceException("Booking reference already exists: " + booking.getBookingReference());
        }

        Booking created = repository.create(booking);
        log.info("Created booking: " + created);
        return created;
    }

    @Transactional
    public Booking update(Booking booking) throws UniqueBookingReferenceException {
        validator.validateBooking(booking);

        Optional<Booking> existing = repository.findByReference(booking.getBookingReference());
        if (existing.isPresent() && !existing.get().getId().equals(booking.getId())) {
            throw new UniqueBookingReferenceException("Booking reference already exists: " + booking.getBookingReference());
        }

        Booking updated = repository.update(booking);
        log.info("Updated booking: " + updated);
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(id);
        log.info("Deleted booking id: " + id);
    }

    public long count() {
        return repository.count();
    }
}
