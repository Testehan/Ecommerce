package com.testehan.ecommerce.backend.brand;

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

    public Page<Brand> listBrandsByPage(int pageNumber, String sortField, String sortOrder, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equalsIgnoreCase("ASC") ? sort.ascending() : sort.descending();

        // the first pageNumber displayed in the UI is 1...but the paging starts from 0, hence why we need to substract 1
        Pageable pageable = PageRequest.of(pageNumber -1, BRAND_PAGE_SIZE, sort);

        if (Strings.isNotBlank(keyword)){
            return brandRepository.findAllByKeyword(keyword,pageable);
        } else {
            return brandRepository.findAll(pageable);
        }
    }
}
