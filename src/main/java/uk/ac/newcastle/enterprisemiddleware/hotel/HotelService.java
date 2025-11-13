package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class HotelService {

    private static final Logger log = Logger.getLogger(HotelService.class);

    @Inject
    HotelRepository repository;

    public Optional<Hotel> findById(Long id) {
        return repository.findById(id);
    }

    public List<Hotel> findAll(int start, int max) {
        return repository.findAll(start, max);
    }

    @Transactional
    public Hotel create(Hotel hotel) throws UniqueHotelNameException {
        // basic normalization
        if (hotel.getName() != null) {
            hotel.setName(hotel.getName().trim());
        }

        // uniqueness check
        if (repository.findByName(hotel.getName()).isPresent()) {
            throw new UniqueHotelNameException("Hotel name already exists: " + hotel.getName());
        }

        Hotel created = repository.create(hotel);
        log.info("Created hotel: " + created);
        return created;
    }

    @Transactional
    public Hotel update(Hotel hotel) throws UniqueHotelNameException {
        if (hotel.getName() != null) {
            hotel.setName(hotel.getName().trim());
        }

        Optional<Hotel> existing = repository.findByName(hotel.getName());
        if (existing.isPresent() && !existing.get().getId().equals(hotel.getId())) {
            throw new UniqueHotelNameException("Hotel name already exists: " + hotel.getName());
        }

        Hotel updated = repository.update(hotel);
        log.info("Updated hotel: " + updated);
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(id);
        log.info("Deleted hotel id: " + id);
    }

    public long count() {
        return repository.count();
    }
}
