package hu.nye.invoice_app.repository;

import hu.nye.invoice_app.domain.norm.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
}
