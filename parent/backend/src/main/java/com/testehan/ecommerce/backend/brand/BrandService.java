package com.testehan.ecommerce.backend.brand;

import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.common.entity.Brand;
import com.testehan.ecommerce.common.exception.BrandNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BrandService {

    public static final int BRAND_PAGE_SIZE=5;

    private BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> findAll() {
       return brandRepository.findAll();
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand findById(Integer id) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).get();
        } catch(NoSuchElementException e){
            throw new BrandNotFoundException("Could not find any brand with id " + id);
        }
    }

    public void deleteBrand(Integer id) throws BrandNotFoundException {
        Long count = brandRepository.countById(id);
        if (count == null || count == 0){
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }

        brandRepository.deleteById(id);
    }

    public boolean isNameUnique(Integer id, String name) {
        Brand brandByName = brandRepository.getBrandByName(name);

        if (brandByName == null){
            return true;
        }

        boolean isCreatingNew = id == null;
        if (isCreatingNew){
            if (brandByName != null){
                return false;           // this means that we try to create a new brand, but the name is already present in the DB
            }
        } else {
            if (brandByName.getId()!=id){
                return false;
            }
        }

        return true;
    }

    public void listBrandsByPage(int pageNumber, PagingAndSortingHelper pagingAndSortingHelper){
        pagingAndSortingHelper.listEntities(pageNumber,BRAND_PAGE_SIZE,brandRepository);
    }
}
