package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.Product;
import hu.nye.invoice_app.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepositoryParam) {
        this.productRepository = productRepositoryParam;
    }

    public List<Product> findAll() {

        return productRepository.findAll();
    }

    public Optional<Product> findById(UUID id) {

        return productRepository.findById(id);
    }

    public Product save(Product product) {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    "A product with this name already exists."
            );
        }
    }

    public Product update(UUID id, String newName) {
        Optional<Product> optionalProduct =
                productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new IllegalArgumentException("Product not found");
        }
        Product product = optionalProduct.get();
        product.setName(newName);
        return productRepository.save(product);
    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return productRepository.findByName(name).isPresent();
    }
}
