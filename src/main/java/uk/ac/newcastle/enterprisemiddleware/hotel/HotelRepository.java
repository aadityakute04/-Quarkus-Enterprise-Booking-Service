package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HotelRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Hotel> findById(Long id) {
        return Optional.ofNullable(em.find(Hotel.class, id));
    }

    public Optional<Hotel> findByName(String name) {
        try {
            Hotel h = em.createQuery("SELECT h FROM Hotel h WHERE h.name = :name", Hotel.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.ofNullable(h);
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    public List<Hotel> findAll(int start, int max) {
        return em.createQuery("SELECT h FROM Hotel h ORDER BY h.name", Hotel.class)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
    }

    @Transactional
    public Hotel create(Hotel hotel) {
        em.persist(hotel);
        return hotel;
    }

    @Transactional
    public Hotel update(Hotel hotel) {
        return em.merge(hotel);
    }

    @Transactional
    public void delete(Long id) {
        Hotel ref = em.find(Hotel.class, id);
        if (ref != null) {
            em.remove(ref);
        }
    }

    public long count() {
        return em.createQuery("SELECT COUNT(h) FROM Hotel h", Long.class).getSingleResult();
    }
}
