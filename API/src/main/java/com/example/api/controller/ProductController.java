package com.example.api.controller;

import com.example.api.model.Product;
import com.example.api.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Obtener todos los productos
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo producto
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.getProductById(id).isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Actualizar un producto
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (!id.equals(product.getId())) {
            return ResponseEntity.badRequest().build();  // Si el ID no coincide, devuelve un error 400
        }

        Optional<Product> existingProduct = productService.getProductById(id);
        if (existingProduct.isPresent()) {
            // El ID es el mismo y el producto existe, por lo que actualizamos
            Product updatedProduct = productService.saveProduct(product);
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();  // Si no se encuentra el producto, devuelve un 404
    }
}
