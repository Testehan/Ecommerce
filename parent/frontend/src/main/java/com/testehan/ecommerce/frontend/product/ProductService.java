package com.testehan.ecommerce.frontend.product;

import com.testehan.ecommerce.common.entity.Product;
import com.testehan.ecommerce.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ProductService {

    public static final Integer PRODUCTS_PER_PAGE = 10;
    public static final Integer PRODUCT_SEARCH_RESULTS_PER_PAGE = 10;
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNumber, int categoryId){
        var categoryIdMatch = "-"+categoryId+"-";
        Pageable pageable = PageRequest.of(pageNumber-1,PRODUCTS_PER_PAGE);

        return productRepository.listByCategory(categoryId,categoryIdMatch,pageable);
    }

    public Product getProductByAlias(String alias) throws ProductNotFoundException {
        var product = productRepository.findByAlias(alias);
        if (Objects.isNull(product)){
            throw new ProductNotFoundException("Product with alias " + alias + " not found!");
        }

        return product;
    }

    public Page<Product> search(String keyword, int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber-1, PRODUCT_SEARCH_RESULTS_PER_PAGE);
        return productRepository.search(keyword,pageable);
    }
}
