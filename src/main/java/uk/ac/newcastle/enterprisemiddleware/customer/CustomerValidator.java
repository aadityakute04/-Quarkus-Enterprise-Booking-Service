package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.*;
import java.util.Set;

@ApplicationScoped
public class CustomerValidator {

    @Inject
    Validator validator;

    public void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer was null.");
        }

        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<Customer> cv : violations) {
                sb.append(cv.getPropertyPath()).append(" ").append(cv.getMessage()).append("; ");
            }
            throw new ConstraintViolationException("Customer validation failed: " + sb.toString(), violations);
        }

        if (customer.getEmail() != null) {
            customer.setEmail(customer.getEmail().trim().toLowerCase());
        }
    }
}
