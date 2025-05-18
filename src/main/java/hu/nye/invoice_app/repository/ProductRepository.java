package hu.nye.invoice_app.repository;

import hu.nye.invoice_app.domain.norm.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByName(String name);
}
