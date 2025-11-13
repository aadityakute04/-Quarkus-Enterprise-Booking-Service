package uk.ac.newcastle.enterprisemiddleware.travel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class TravelAgentBookingRepository {

    @Inject
    EntityManager em;

    public void persist(TravelAgentBooking t) {
        em.persist(t);
    }

    public TravelAgentBooking findById(Long id) {
        return em.find(TravelAgentBooking.class, id);
    }

    public void update(TravelAgentBooking t) {
        em.merge(t);
    }

    public void delete(TravelAgentBooking t) {
        em.remove(em.contains(t) ? t : em.merge(t));
    }

    public List<TravelAgentBooking> findAll(int start, int size) {
        TypedQuery<TravelAgentBooking> q = em.createQuery("SELECT t FROM TravelAgentBooking t ORDER BY t.id", TravelAgentBooking.class);
        q.setFirstResult(start);
        q.setMaxResults(size);
        return q.getResultList();
    }
}
