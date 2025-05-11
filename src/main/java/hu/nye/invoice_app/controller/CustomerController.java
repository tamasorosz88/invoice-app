package hu.nye.invoice_app.controller;

import hu.nye.invoice_app.domain.norm.Customer;
import hu.nye.invoice_app.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerServiceParam) {
        this.customerService = customerServiceParam;
    }

    @GetMapping
    public String getAllCustomers(Model model) {
        List<Customer> customers = customerService.findAll();
        if (customers.isEmpty()) {
            model.addAttribute("message",
                    "No customers available.");
        } else {
            model.addAttribute("customers", customers);
        }
        return "customer/list";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
        Optional<Customer> customer = customerService.findById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public String createCustomer(@ModelAttribute Customer customer,
                                 Model model) {
        try {
            customerService.save(customer);
            return "redirect:/api/customers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "customer/create";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable UUID id,
                                 @ModelAttribute Customer updatedCustomer,
                                 Model model) {
        try {
            customerService.update(id, updatedCustomer);
            return "redirect:/api/customers";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("customer", updatedCustomer);
            return "customer/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable UUID id, Model model) {
        try {
            customerService.delete(id);
            return "redirect:/api/customers";
        } catch (Exception e) {
            model.addAttribute("message", "Failed to delete the customer.");
            return "customer/list";
        }
    }

    @GetMapping("/create")
    public String showCreateCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditCustomerForm(@PathVariable UUID id, Model model) {
        Optional<Customer> customer = customerService.findById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "customer/edit";
        } else {
            model.addAttribute("message", "Customer not found.");
            return "redirect:/api/customers";
        }
    }
}
