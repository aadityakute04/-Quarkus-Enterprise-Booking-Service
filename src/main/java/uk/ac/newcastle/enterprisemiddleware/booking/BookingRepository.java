package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookingRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(em.find(Booking.class, id));
    }

    public Optional<Booking> findByReference(String ref) {
        try {
            Booking b = em.createQuery("SELECT b FROM Booking b WHERE b.bookingReference = :ref", Booking.class)
                    .setParameter("ref", ref)
                    .getSingleResult();
            return Optional.ofNullable(b);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Booking> findAll(int start, int max) {
        return em.createQuery("SELECT b FROM Booking b ORDER BY b.id", Booking.class)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
    }

    @Transactional
    public Booking create(Booking booking) {
        em.persist(booking);
        return booking;
    }

    @Transactional
    public Booking update(Booking booking) {
        return em.merge(booking);
    }

    @Transactional
    public void delete(Long id) {
        Booking ref = em.find(Booking.class, id);
        if (ref != null) {
            em.remove(ref);
        }
    }

    public long count() {
        return em.createQuery("SELECT COUNT(b) FROM Booking b", Long.class).getSingleResult();
    }
}
