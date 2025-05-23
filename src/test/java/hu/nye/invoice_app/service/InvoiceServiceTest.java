package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.Customer;
import hu.nye.invoice_app.domain.norm.Invoice;
import hu.nye.invoice_app.domain.norm.InvoiceItem;
import hu.nye.invoice_app.domain.norm.Product;
import hu.nye.invoice_app.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private InvoiceService invoiceService;

    public InvoiceServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // given
        List<Invoice> invoices = new ArrayList<>();
        when(invoiceRepository.findAll()).thenReturn(invoices);

        // when
        List<Invoice> result = invoiceService.findAll();

        // then
        assertEquals(invoices, result);
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        // given
        UUID id = UUID.randomUUID();
        Invoice invoice = new Invoice();
        when(invoiceRepository.findById(id)).thenReturn(Optional.of(invoice));

        // when
        Optional<Invoice> result = invoiceService.findById(id);

        // then
        assertTrue(result.isPresent());
        assertEquals(invoice, result.get());
        verify(invoiceRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(invoiceRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Invoice> result = invoiceService.findById(id);

        // then
        assertFalse(result.isPresent());
        verify(invoiceRepository, times(1)).findById(id);
    }

    @Test
    void testCreateInvoice_Success() {
        // given
        UUID customerId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID());
        List<Integer> quantities = List.of(1);
        List<BigDecimal> unitPrices = List.of(BigDecimal.valueOf(100));

        Customer customer = new Customer();
        Product product = new Product();
        Invoice savedInvoice = new Invoice();

        when(customerService.findById(customerId)).thenReturn(Optional.of(customer));
        when(productService.findById(productIds.get(0))).thenReturn(Optional.of(product));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(savedInvoice);

        // when
        invoiceService.createInvoice(customerId, productIds, quantities, unitPrices);

        // then
        verify(customerService, times(1)).findById(customerId);
        verify(productService, times(1)).findById(productIds.get(0));
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void testCreateInvoice_CustomerNotFound() {
        // given
        UUID customerId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID());
        List<Integer> quantities = List.of(1);
        List<BigDecimal> unitPrices = List.of(BigDecimal.valueOf(100));

        when(customerService.findById(customerId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                invoiceService.createInvoice(customerId, productIds, quantities, unitPrices));
        assertEquals("Customer not found", exception.getMessage());
        verify(customerService, times(1)).findById(customerId);
        verifyNoInteractions(productService, invoiceRepository);
    }

    @Test
    void testUpdateInvoiceItems_Success() {
        // given
        UUID invoiceId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID());
        List<Integer> quantities = List.of(1);
        List<BigDecimal> unitPrices = List.of(BigDecimal.valueOf(100));

        Invoice invoice = new Invoice();
        invoice.setItems(new ArrayList<>());
        Product product = new Product();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(productService.findById(productIds.get(0))).thenReturn(Optional.of(product));

        // when
        invoiceService.updateInvoiceItems(invoiceId, productIds, quantities, unitPrices);

        // then
        verify(invoiceRepository, times(1)).findById(invoiceId);
        verify(productService, times(1)).findById(productIds.get(0));
        verify(invoiceRepository, times(1)).save(invoice);
        assertEquals(1, invoice.getItems().size());
    }

    @Test
    void testUpdateInvoiceItems_InvoiceNotFound() {
        // given
        UUID invoiceId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID());
        List<Integer> quantities = List.of(1);
        List<BigDecimal> unitPrices = List.of(BigDecimal.valueOf(100));

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                invoiceService.updateInvoiceItems(invoiceId, productIds, quantities, unitPrices));
        assertEquals("Invoice not found", exception.getMessage());
        verify(invoiceRepository, times(1)).findById(invoiceId);
        verifyNoInteractions(productService);
    }

    @Test
    void testUpdateInvoiceItems_MismatchedListSizes() {
        // given
        UUID invoiceId = UUID.randomUUID();
        List<UUID> productIds = List.of(UUID.randomUUID());
        List<Integer> quantities = List.of(1);
        List<BigDecimal> unitPrices = List.of();

        Invoice invoice = new Invoice();
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                invoiceService.updateInvoiceItems(invoiceId, productIds, quantities, unitPrices));
        assertEquals("Mismatched list sizes for products, quantities, and unit prices", exception.getMessage());
        verify(invoiceRepository, times(1)).findById(invoiceId);
        verifyNoInteractions(productService);
    }
}
