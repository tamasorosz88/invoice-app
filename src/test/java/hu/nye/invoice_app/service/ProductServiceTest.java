package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.Product;
import hu.nye.invoice_app.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllProducts() {
        // given
        when(productRepository.findAll()).thenReturn(List.of());

        // when
        var result = productService.findAll();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testFindProductById_Found() {
        // given
        UUID id = UUID.randomUUID();
        Product product = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        Optional<Product> result = productService.findById(id);

        // then
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void testFindProductById_NotFound() {
        // given
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Product> result = productService.findById(id);

        // then
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    void testSaveProduct_Success() {
        // given
        Product product = new Product();
        when(productRepository.save(product)).thenReturn(product);

        // when
        Product result = productService.save(product);

        // then
        assertNotNull(result);
        assertEquals(product, result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testSaveProduct_DataIntegrityViolation() {
        // given
        Product product = new Product();
        when(productRepository.save(product)).thenThrow(new DataIntegrityViolationException("Duplicate name"));

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.save(product));
        assertEquals("A product with this name already exists.", exception.getMessage());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct_Success() {
        // given
        UUID id = UUID.randomUUID();
        String newName = "Updated Name";
        Product product = new Product();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // when
        Product result = productService.update(id, newName);

        // then
        assertNotNull(result);
        assertEquals(newName, product.getName());
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProduct_NotFound() {
        // given
        UUID id = UUID.randomUUID();
        String newName = "Updated Name";
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> productService.update(id, newName));
        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct() {
        // given
        UUID id = UUID.randomUUID();
        doNothing().when(productRepository).deleteById(id);

        // when
        productService.delete(id);

        // then
        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    void testExistsByName_Found() {
        // given
        String name = "Test Product";
        when(productRepository.findByName(name)).thenReturn(Optional.of(new Product()));

        // when
        boolean result = productService.existsByName(name);

        // then
        assertTrue(result);
        verify(productRepository, times(1)).findByName(name);
    }

    @Test
    void testExistsByName_NotFound() {
        // given
        String name = "Test Product";
        when(productRepository.findByName(name)).thenReturn(Optional.empty());

        // when
        boolean result = productService.existsByName(name);

        // then
        assertFalse(result);
        verify(productRepository, times(1)).findByName(name);
    }
}
