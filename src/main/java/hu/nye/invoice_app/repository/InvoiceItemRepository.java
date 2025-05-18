package hu.nye.invoice_app.repository;

import hu.nye.invoice_app.domain.norm.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceItemRepository
        extends JpaRepository<InvoiceItem, UUID> {
}
