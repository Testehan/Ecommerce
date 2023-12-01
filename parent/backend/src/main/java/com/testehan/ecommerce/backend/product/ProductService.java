package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.common.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@Transactional
public class ProductService {

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
}
