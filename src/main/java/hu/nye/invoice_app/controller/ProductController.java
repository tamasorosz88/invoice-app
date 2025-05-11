package hu.nye.invoice_app.controller;

import hu.nye.invoice_app.domain.norm.Product;
import hu.nye.invoice_app.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productServiceParam) {
        this.productService = productServiceParam;
    }

    @GetMapping
    public String getAllProducts(Model model) {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            model.addAttribute("message", "No products available.");
        } else {
            model.addAttribute("products", products);
        }
        return "product/list";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public String createProduct(@ModelAttribute Product product, Model model) {
        try {
            productService.save(product);
            return "redirect:/api/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("product", product);
            model.addAttribute("error", e.getMessage());
            return "product/create";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable UUID id,
            @RequestParam String newName,
            Model model) {
        try {
            productService.update(id, newName);
            return "redirect:/api/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "product/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable UUID id, Model model) {
        try {
            productService.delete(id);
            return "redirect:/api/products";
        } catch (Exception e) {
            model.addAttribute("message", "Failed to delete the product.");
            return "product/list";
        }
    }

    @GetMapping("/create")
    public String showCreateProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable UUID id, Model model) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product/edit";
        } else {
            model.addAttribute("message", "Product not found.");
            return "redirect:/api/products";
        }
    }
}
