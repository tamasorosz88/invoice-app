package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.InvoiceItem;
import hu.nye.invoice_app.domain.norm.Product;
import hu.nye.invoice_app.repository.InvoiceItemRepository;
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

class InvoiceItemServiceTest {

    @Mock
    private InvoiceItemRepository invoiceItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private InvoiceItemService invoiceItemService;

    public InvoiceItemServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        // given
        List<InvoiceItem> items = new ArrayList<>();
        when(invoiceItemRepository.findAll()).thenReturn(items);

        // when
        List<InvoiceItem> result = invoiceItemService.findAll();

        // then
        assertEquals(items, result);
        verify(invoiceItemRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        // given
        UUID id = UUID.randomUUID();
        InvoiceItem item = new InvoiceItem();
        when(invoiceItemRepository.findById(id)).thenReturn(Optional.of(item));

        // when
        Optional<InvoiceItem> result = invoiceItemService.findById(id);

        // then
        assertTrue(result.isPresent());
        assertEquals(item, result.get());
        verify(invoiceItemRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(invoiceItemRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<InvoiceItem> result = invoiceItemService.findById(id);

        // then
        assertFalse(result.isPresent());
        verify(invoiceItemRepository, times(1)).findById(id);
    }

    @Test
    void testSaveInvoiceItem() {
        // given
        InvoiceItem item = new InvoiceItem();
        when(invoiceItemRepository.save(item)).thenReturn(item);

        // when
        InvoiceItem result = invoiceItemService.save(item);

        // then
        assertEquals(item, result);
        verify(invoiceItemRepository, times(1)).save(item);
    }

    @Test
    void testDeleteInvoiceItem() {
        // given
        UUID id = UUID.randomUUID();
        doNothing().when(invoiceItemRepository).deleteById(id);

        // when
        invoiceItemService.delete(id);

        // then
        verify(invoiceItemRepository, times(1)).deleteById(id);
    }

    @Test
    void testUpdateInvoiceItem_Success() {
        // given
        UUID id = UUID.randomUUID();
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Test Product");

        InvoiceItem item = new InvoiceItem();
        item.setId(id);
        item.setProduct(product);
        item.setQuantity(5);
        item.setUnitPrice(BigDecimal.valueOf(100));

        when(invoiceItemRepository.findById(id)).thenReturn(Optional.of(item));
        when(productService.findById(product.getId())).thenReturn(Optional.of(product));

        // when
        invoiceItemService.update(id, item);

        // then
        verify(invoiceItemRepository, times(1)).findById(id);
        verify(invoiceItemRepository, times(1)).save(item);
        assertEquals(product, item.getProduct());
        assertEquals(5, item.getQuantity());
        assertEquals(BigDecimal.valueOf(100), item.getUnitPrice());
    }
}
