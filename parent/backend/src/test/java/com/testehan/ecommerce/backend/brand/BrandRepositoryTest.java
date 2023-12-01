package com.testehan.ecommerce.backend.brand;

import com.testehan.ecommerce.backend.category.CategoryRepository;
import com.testehan.ecommerce.common.entity.Brand;
import com.testehan.ecommerce.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void whenBrandIsCreated_brandIsCreatedWithSuccessAndCanBeRetrievedById(){
        var brand = new Brand("Puma","puma_logo.png",new HashSet<>());

        var savedBrand = brandRepository.save(brand);

        var retrievedBrand = brandRepository.findById(savedBrand.getId()).get();

        assertThat(retrievedBrand.getName()).isEqualTo(brand.getName());
        assertThat(retrievedBrand.getLogo()).isEqualTo(brand.getLogo());
        assertThat(retrievedBrand.getId()).isGreaterThan(0);
        assertThat(savedBrand.getCategories().size()).isEqualTo(0);
    }

    @Test
    public void whenBrandWith2CategoriesIsCreated_brandIsCreatedWithSuccess(){
        var category1 = new Category("Tablets","Tablets","a pic of tablets.jpg");
        var category2 = new Category("Phones","Phones","a pic of Phones.jpg");
        var brand = new Brand("Apple","apple_logo.png",new HashSet<>());

        var savedCategory1= categoryRepository.save(category1);
        var savedCategory2 = categoryRepository.save(category2);
        brand.getCategories().add(savedCategory1);
        brand.getCategories().add(savedCategory2);
        var savedBrand = brandRepository.save(brand);

        assertThat(savedBrand.getName()).isEqualTo(brand.getName());
        assertThat(savedBrand.getLogo()).isEqualTo(brand.getLogo());
        assertThat(savedBrand.getId()).isGreaterThan(0);
        assertThat(savedBrand.getCategories().size()).isEqualTo(2);
    }

    @Test
    public void whenBrandIsUpdated_changesAreStoredInDB(){
        var brand = new Brand("Nike","nikkke_logo.png",new HashSet<>());

        var savedBrand = brandRepository.save(brand);

        savedBrand.setLogo("nike_logo.png");
        savedBrand = brandRepository.save(brand);

        assertThat(savedBrand.getName()).isEqualTo("Nike");
        assertThat(savedBrand.getLogo()).isEqualTo("nike_logo.png");
        assertThat(savedBrand.getId()).isGreaterThan(0);
        assertThat(savedBrand.getCategories().size()).isEqualTo(0);
    }

    @Test
    public void whenBrandIsDeleted_changesAreStoredInDB(){
        var brand = new Brand("Armani","Armani.png",new HashSet<>());

        var savedBrand = brandRepository.save(brand);

        brandRepository.deleteById(savedBrand.getId());

        var brands = brandRepository.findAll();
        assertThat(brands.contains(brand)).isEqualTo(false);
    }

    @Test
    public void whenBrandsAreRetrieved_brandsAreRetrievedOrderedByName(){
        var listBrands = brandRepository.findAll();
        listBrands.forEach(System.out::println);
    }
}
