package com.example.api.service;

import com.example.api.model.Product;
import com.example.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;


import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        try {
            Optional<Product> product = productRepository.findById(id);
            if (product.isPresent()) {
                productRepository.deleteById(id);
            } else {
                throw new EntityNotFoundException("Product not found");
            }
        } catch (Exception e) {
            // Log the exception for debugging
            throw new RuntimeException("Error while deleting the product", e);
        }
    }
}
