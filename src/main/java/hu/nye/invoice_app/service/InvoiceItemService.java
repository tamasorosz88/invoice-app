package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.InvoiceItem;
import hu.nye.invoice_app.repository.InvoiceItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceItemService {
    private final InvoiceItemRepository invoiceItemRepository;

    public InvoiceItemService(
            InvoiceItemRepository invoiceItemRepositoryParam) {
        this.invoiceItemRepository = invoiceItemRepositoryParam;
    }

    public List<InvoiceItem> findAll() {

        return invoiceItemRepository.findAll();
    }

    public Optional<InvoiceItem> findById(UUID id) {

        return invoiceItemRepository.findById(id);
    }

    public InvoiceItem save(InvoiceItem invoiceItem) {

        return invoiceItemRepository.save(invoiceItem);
    }

    public InvoiceItem update(UUID id, InvoiceItem updatedInvoiceItem) {
        Optional<InvoiceItem> optionalInvoiceItem =
                invoiceItemRepository.findById(id);
        if (!optionalInvoiceItem.isPresent()) {
            throw new IllegalArgumentException("InvoiceItem not found");
        }
        InvoiceItem invoiceItem = optionalInvoiceItem.get();

        invoiceItem.setProduct(updatedInvoiceItem.getProduct());
        invoiceItem.setQuantity(updatedInvoiceItem.getQuantity());
        invoiceItem.setUnitPrice(updatedInvoiceItem.getUnitPrice());
        return invoiceItemRepository.save(invoiceItem);
    }

    public void delete(UUID id) {
        invoiceItemRepository.deleteById(id);
    }
}
