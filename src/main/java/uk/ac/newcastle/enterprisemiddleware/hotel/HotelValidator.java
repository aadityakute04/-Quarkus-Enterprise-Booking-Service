package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;
import java.util.Optional;

@ApplicationScoped
public class HotelValidator {

    @Inject
    Validator validator;

    @Inject
    HotelRepository repository;

    public void validateHotel(Hotel hotel) throws ConstraintViolationException, UniqueHotelNameException {

        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        Optional<Hotel> existing = repository.findByName(hotel.getName());
        if (existing.isPresent() && (hotel.getId() == null || !existing.get().getId().equals(hotel.getId()))) {
            throw new UniqueHotelNameException("Hotel name '" + hotel.getName() + "' already exists.");
        }
    }
}
