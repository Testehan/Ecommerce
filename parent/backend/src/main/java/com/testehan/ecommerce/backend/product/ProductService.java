package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.common.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> listAllProducts(){
        return productRepository.findAll();
    }
}
