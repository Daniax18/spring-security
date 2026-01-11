package com.daniax.InitSecurityApp.controller;

import com.daniax.InitSecurityApp.dto.ProductDTO;
import com.daniax.InitSecurityApp.entity.Product;
import com.daniax.InitSecurityApp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll(){
        return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductDTO productDto){
        try {
            Product toSave = new Product(productDto.getName(), productDto.getPrice());
            return new ResponseEntity<>(productService.create(toSave), HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
