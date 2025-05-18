package hu.nye.invoice_app.repository;

import hu.nye.invoice_app.domain.norm.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByFirstNameAndLastNameAndEmailAndPhoneNumber(
            String firstName,
            String lastName,
            String email,
            String phoneNumber);
}
