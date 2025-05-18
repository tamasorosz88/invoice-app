package hu.nye.invoice_app.service;

import hu.nye.invoice_app.domain.norm.Customer;
import hu.nye.invoice_app.domain.norm.Invoice;
import hu.nye.invoice_app.domain.norm.Product;
import hu.nye.invoice_app.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import hu.nye.invoice_app.domain.norm.InvoiceItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CustomerService customerService;
    private final ProductService productService;

    public InvoiceService(InvoiceRepository invoiceRepositoryParam,
                          CustomerService customerServiceParam,
                          ProductService productServiceParam) {
        this.invoiceRepository = invoiceRepositoryParam;
        this.customerService = customerServiceParam;
        this.productService = productServiceParam;
    }

    public List<Invoice> findAll() {

        return invoiceRepository.findAll();
    }

    public Optional<Invoice> findById(UUID id) {

        return invoiceRepository.findById(id);
    }

    public Invoice save(Invoice invoice) {

        return invoiceRepository.save(invoice);
    }

    public Invoice update(UUID id,
                          UUID productId,
                          int quantity,
                          BigDecimal unitPrice) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invoice not found"
                ));

        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Invoice has no items to update"
            );
        }

        InvoiceItem item = invoice.getItems().get(0);

        Product product = productService.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found"
                ));

        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);

        return invoiceRepository.save(invoice);
    }

    public void delete(UUID id) {
        invoiceRepository.deleteById(id);
    }

    public void createInvoice(UUID customerId,
                              List<UUID> productIds,
                              List<Integer> quantities,
                              List<BigDecimal> unitPrices) {
        Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found"
                ));

        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);

        List<InvoiceItem> items = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.findById(productIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found"
                    ));

            InvoiceItem item = InvoiceItem.builder()
                    .product(product)
                    .quantity(quantities.get(i))
                    .unitPrice(unitPrices.get(i))
                    .invoice(invoice)
                    .build();

            items.add(item);
        }

        invoice.setItems(items);
        invoiceRepository.save(invoice);
    }

    public void updateInvoiceItems(UUID invoiceId,
                                   List<UUID> productIds,
                                   List<Integer> quantities,
                                   List<BigDecimal> unitPrices) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invoice not found"
                ));

        if (productIds.size() != quantities.size()
                || productIds.size() != unitPrices.size()) {
            throw new IllegalArgumentException(
                    "Mismatched list sizes for products, "
                            + "quantities, and unit prices");
        }

        // Clear the existing items collection
        invoice.getItems().clear();

        // Add the updated items to the collection
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.findById(productIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found"
                    ));

            InvoiceItem item = InvoiceItem.builder()
                    .product(product)
                    .quantity(quantities.get(i))
                    .unitPrice(unitPrices.get(i))
                    .invoice(invoice)
                    .build();

            invoice.getItems().add(item);
        }

        // Save the updated invoice
        invoiceRepository.save(invoice);
    }
}
