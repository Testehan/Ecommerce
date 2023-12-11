package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.common.entity.Product;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 5;

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public List<Product> listAllProducts(){
        return productRepository.findAll();
    }

    public Product save(Product product){
        if (Objects.isNull(product.getId())){
            product.setCreatedTime(new Date());
        }

        if (Objects.isNull(product.getAlias()) || product.getAlias().isEmpty()){
            var defaultAlias = product.getName().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(new Date());

        return productRepository.save(product);
    }

    public boolean isNameUnique(Integer id, String name) {
        Product productByName = productRepository.getProductByName(name);

        if (productByName == null){
            return true;
        }

        boolean isCreatingNew = id == null;
        if (isCreatingNew){
            if (productByName != null){
                return false;           // this means that we try to create a new brand, but the name is already present in the DB
            }
        } else {
            if (productByName.getId()!=id){
                return false;
            }
        }

        return true;
    }

    public void updateEnabledStatus(Integer id, boolean enabled) {
        productRepository.updateEnabledStatus(id,enabled);
    }

    public void delete(Integer id) throws ProductNotFoundException {
        try {
            productRepository.findById(id).get();
        } catch (NoSuchElementException e){
            throw new ProductNotFoundException("Could not find any product with ID " + id);
        }

        productRepository.deleteById(id);
    }

    public Product getById(Integer id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        } catch (NoSuchElementException e){
            throw new ProductNotFoundException("Could not find any product with id " + id);
        }
    }

    public Page<Product> listProductsByPage(int pageNumber, String sortField, String sortOrder, String keyword,Integer categoryId){
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equalsIgnoreCase("ASC") ? sort.ascending() : sort.descending();

        // the first pageNumber displayed in the UI is 1...but the paging starts from 0, hence why we need to substract 1
        Pageable pageable = PageRequest.of(pageNumber -1, PRODUCTS_PER_PAGE, sort);

        if (Strings.isNotBlank(keyword)){
            if (Objects.nonNull(categoryId) && categoryId>0){
                var categoryIdMatch = "-"+ categoryId +"-";
                return productRepository.findAllInCategoryWithKeyword(categoryId,categoryIdMatch,keyword,pageable);
            }
            return productRepository.findAllByKeyword(keyword,pageable);
        }

        if (Objects.nonNull(categoryId) && categoryId>0){
            var categoryIdMatch = "-"+ categoryId +"-";
            return productRepository.findAllInCategory(categoryId,categoryIdMatch,pageable);
        }

        return productRepository.findAll(pageable);

    }
}
