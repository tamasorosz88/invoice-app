package hu.nye.invoice_app.controller;

import hu.nye.invoice_app.domain.norm.Customer;
import hu.nye.invoice_app.domain.norm.Invoice;
import hu.nye.invoice_app.service.CustomerService;
import hu.nye.invoice_app.service.InvoiceService;
import hu.nye.invoice_app.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

@Controller
@RequestMapping("/api/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final CustomerService customerService;
    private final ProductService productService;

    public InvoiceController(InvoiceService invoiceServiceParam,
                             CustomerService customerServiceParam,
                             ProductService productServiceParam) {
        this.invoiceService = invoiceServiceParam;
        this.customerService = customerServiceParam;
        this.productService = productServiceParam;
    }

    @GetMapping
    public String getInvoices(Model model) {
        List<Invoice> invoices = invoiceService.findAll();
        if (invoices.isEmpty()) {
            model.addAttribute("message", "No invoices available.");
        } else {
            model.addAttribute("invoices", invoices);
        }
        return "invoice/list";
    }

    @GetMapping("/{id}")
    public String getInvoiceById(@PathVariable UUID id, Model model) {
        Optional<Invoice> invoice = invoiceService.findById(id);
        if (invoice.isPresent()) {
            model.addAttribute("invoice", invoice.get());
            return "invoice/details";
        } else {
            model.addAttribute("message", "Invoice not found.");
            return "redirect:/api/invoices";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable UUID id) {
        invoiceService.delete(id);
        return "redirect:/api/invoices";
    }

    @GetMapping("/create")
    public String showCreateInvoiceForm(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) Integer productCount,
            Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("products", productService.findAll());

        if (customerId != null) {
            Optional<Customer> selectedCustomer =
                    customerService.findById(customerId);
            selectedCustomer.ifPresent(
                    customer -> model.addAttribute(
                            "selectedCustomer", customer));
        }

        if (productCount != null) {
            model.addAttribute("productCount", productCount);
        }

        return "invoice/create";
    }

    @PostMapping
    public String createInvoice(@RequestParam UUID customerId,
                                @RequestParam List<UUID> productIds,
                                @RequestParam List<Integer> quantities,
                                @RequestParam List<BigDecimal> unitPrices) {
        invoiceService.createInvoice(
                customerId,
                productIds,
                quantities,
                unitPrices);
        return "redirect:/api/invoices";
    }

    @GetMapping("/edit/{id}")
    public String showEditInvoiceForm(@PathVariable UUID id, Model model) {
        Optional<Invoice> invoice = invoiceService.findById(id);
        if (invoice.isPresent()) {
            model.addAttribute("invoice", invoice.get());
            model.addAttribute("products", productService.findAll());
            return "invoice/edit";
        } else {
            model.addAttribute("message", "Invoice not found.");
            return "redirect:/api/invoices";
        }
    }

    @PostMapping("/update/{id}")
    public String updateInvoice(@PathVariable UUID id,
                                @RequestParam List<UUID> productIds,
                                @RequestParam List<Integer> quantities,
                                @RequestParam List<BigDecimal> unitPrices) {
        try {
            invoiceService.updateInvoiceItems(id, productIds,
                    quantities,
                    unitPrices);
            return "redirect:/api/invoices";
        } catch (IllegalArgumentException e) {
            return "redirect:/api/invoices/edit/" + id
                    + "?error=" + e.getMessage();
        }
    }
}
