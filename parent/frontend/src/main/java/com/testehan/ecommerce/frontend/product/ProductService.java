package com.testehan.ecommerce.frontend.product;

import com.testehan.ecommerce.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public static final Integer PRODUCTS_PER_PAGE = 10;
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNumber, int categoryId){
        var categoryIdMatch = "-"+categoryId+"-";
        Pageable pageable = PageRequest.of(pageNumber-1,PRODUCTS_PER_PAGE);

        return productRepository.listByCategory(categoryId,categoryIdMatch,pageable);
    }
}
