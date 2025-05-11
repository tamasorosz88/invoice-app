package hu.nye.invoice_app.controller;

import hu.nye.invoice_app.domain.norm.InvoiceItem;
import hu.nye.invoice_app.service.InvoiceItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoice-items")
public class InvoiceItemController {
    private final InvoiceItemService invoiceItemService;

    public InvoiceItemController(InvoiceItemService invoiceItemServiceParam) {
        this.invoiceItemService = invoiceItemServiceParam;
    }

    @GetMapping
    public List<InvoiceItem> getAllInvoiceItems() {
        return invoiceItemService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceItem> getInvoiceItemById(
            @PathVariable UUID id) {
        Optional<InvoiceItem> invoiceItem = invoiceItemService.findById(id);
        return invoiceItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InvoiceItem createInvoiceItem(@RequestBody InvoiceItem invoiceItem) {
        return invoiceItemService.save(invoiceItem);
    }

    @PutMapping("/{id}")
    public InvoiceItem updateInvoiceItem(
            @PathVariable UUID id,
            @RequestBody InvoiceItem updatedInvoiceItem) {
        return invoiceItemService.update(id, updatedInvoiceItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoiceItem(@PathVariable UUID id) {
        invoiceItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
