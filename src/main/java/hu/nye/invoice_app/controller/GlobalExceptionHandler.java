package hu.nye.invoice_app.controller;

import hu.nye.invoice_app.domain.norm.Customer;
import hu.nye.invoice_app.service.CustomerService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final CustomerService customerService;

    public GlobalExceptionHandler(CustomerService customerServiceParam) {
        this.customerService = customerServiceParam;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(
            ConstraintViolationException ex,
            Model model,
            HttpServletRequest request) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        StringBuilder errorMessage = new StringBuilder();

        boolean relevantFieldFound = false;
        for (ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString();
            errorMessage.append(field).append(": ")
                    .append(violation.getMessage())
                    .append("; ");
            if (field.equals("firstName")
                    || field.equals("lastName")
                    || field.equals("email")
                    || field.equals("phoneNumber")) {
                relevantFieldFound = true;
            }
        }

        model.addAttribute("error", errorMessage.toString());

        if (!relevantFieldFound) {
            return "error";
        }

        String referer = request.getHeader("Referer");
        System.out.println("A referer értéke: " + referer);
        if (referer != null) {
            if (referer.contains("/edit")) {
                String id = referer.substring(referer.lastIndexOf("/") + 1);
                try {
                    UUID customerId = UUID.fromString(id);
                    Optional<Customer> customerOptional = customerService
                            .findById(customerId);
                    if (customerOptional.isPresent()) {
                        model.addAttribute("customer",
                                customerOptional.get());
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(
                            "Invalid customer ID in referer: " + id);
                }
                return "customer/edit";
            } else if (referer.contains("/create")) {
                return "customer/create";
            }
        }

        return "error";
    }
}
