package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Customer> findById(Long id) {
        return Optional.ofNullable(em.find(Customer.class, id));
    }

    public Optional<Customer> findByEmail(String email) {
        try {
            Customer c = em.createQuery("SELECT c FROM Customer c WHERE c.email = :email", Customer.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.ofNullable(c);
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    public List<Customer> findAll(int start, int max) {
        return em.createQuery("SELECT c FROM Customer c ORDER BY c.id", Customer.class)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
    }

    @Transactional
    public Customer create(Customer customer) {
        em.persist(customer);
        return customer;
    }

    @Transactional
    public Customer update(Customer customer) {
        return em.merge(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer ref = em.find(Customer.class, id);
        if (ref != null) {
            em.remove(ref);
        }
    }

    public long count() {
        return em.createQuery("SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult();
    }
}
