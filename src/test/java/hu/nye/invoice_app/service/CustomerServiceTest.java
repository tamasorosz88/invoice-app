package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.Customer;
import hu.nye.invoice_app.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    public CustomerServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // given
        List<Customer> customers = new ArrayList<>();
        when(customerRepository.findAll()).thenReturn(customers);

        // when
        List<Customer> result = customerService.findAll();

        // then
        assertEquals(customers, result);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        // given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // when
        Optional<Customer> result = customerService.findById(id);

        // then
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Customer> result = customerService.findById(id);

        // then
        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(id);
    }

    @Test
    void testSaveCustomer() {
        // given
        Customer customer = new Customer();
        when(customerRepository.save(customer)).thenReturn(customer);

        // when
        Customer result = customerService.save(customer);

        // then
        assertEquals(customer, result);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testDeleteCustomer() {
        // given
        UUID id = UUID.randomUUID();
        doNothing().when(customerRepository).deleteById(id);

        // when
        customerService.delete(id);

        // then
        verify(customerRepository, times(1)).deleteById(id);
    }
}
