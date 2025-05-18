package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.Customer;
import hu.nye.invoice_app.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepositoryParam) {
        this.customerRepository = customerRepositoryParam;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(UUID id) {
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) {
        if (customerRepository.findByFirstNameAndLastNameAndEmailAndPhoneNumber(
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException(
                    "A customer with this name, email, and phone number "
                            + "already exists."
            );
        }
        return customerRepository.save(customer);
    }

    public Customer update(UUID id, Customer updatedCustomer) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (!optionalCustomer.isPresent()) {
            throw new IllegalArgumentException("Customer not found");
        }
        Customer customer = optionalCustomer.get();
        customer.setFirstName(updatedCustomer.getFirstName());
        customer.setLastName(updatedCustomer.getLastName());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        customer.setEmail(updatedCustomer.getEmail());
        return customerRepository.save(customer);
    }

    public void delete(UUID id) {
        customerRepository.deleteById(id);
    }
}
