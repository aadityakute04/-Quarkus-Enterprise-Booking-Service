package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CustomerService {

    private static final Logger log = Logger.getLogger(CustomerService.class);

    @Inject
    CustomerRepository repository;

    @Inject
    CustomerValidator validator;

    public Optional<Customer> findById(Long id) {
        return repository.findById(id);
    }

    public List<Customer> findAll(int start, int max) {
        return repository.findAll(start, max);
    }

    @Transactional
    public Customer create(Customer customer) throws UniqueEmailException {
        validator.validateCustomer(customer);

        if (repository.findByEmail(customer.getEmail()).isPresent()) {
            throw new UniqueEmailException("Email already exists: " + customer.getEmail());
        }

        Customer created = repository.create(customer);
        log.info("Created customer: " + created);
        return created;
    }

    @Transactional
    public Customer update(Customer customer) throws UniqueEmailException {
        validator.validateCustomer(customer);

        Optional<Customer> existing = repository.findByEmail(customer.getEmail());
        if (existing.isPresent() && !existing.get().getId().equals(customer.getId())) {
            throw new UniqueEmailException("Email already in use: " + customer.getEmail());
        }

        Customer updated = repository.update(customer);
        log.info("Updated customer: " + updated);
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(id);
        log.info("Deleted customer id: " + id);
    }

    public long count() {
        return repository.count();
    }
}
