package com.testehan.ecommerce.backend.product;

import com.testehan.ecommerce.backend.brand.BrandRepository;
import com.testehan.ecommerce.backend.category.CategoryRepository;
import com.testehan.ecommerce.common.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void whenPersistingProduct_productIsPersistedInDB(){
        var category = new Category("Test_Products","Test_Products","a pic of various products.jpg");
        var savedCategory = categoryRepository.save(category);
        var brand = new Brand("Test_Brand","puma_logo.png",new HashSet<>());
        brand.getCategories().add(savedCategory);
        var savedBrand = brandRepository.save(brand);

        Product product = new Product();
        product.setName("Iphone 17");
        product.setAlias("Iphone 17");
        product.setShortDescription("Latest Iphone from Apple with improved specs.");
        product.setFullDescription("Tim Cook talking about this phone");

        product.setBrand(savedBrand);
        product.setCategory(savedCategory);

        product.setPrice(200000);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        var savedProduct = productRepository.save(product);

        assertThat(savedProduct.getName()).isEqualTo("Iphone 17");
        assertThat(savedProduct.getAlias()).isEqualTo("Iphone 17");
        assertThat(savedProduct.getBrand()).isEqualTo(savedBrand);
        assertThat(savedProduct.getCategory()).isEqualTo( savedCategory);
    }

    @Test
    public void whenPersistingProductWithImages_productImagesArePersistedInDB(){
        var category = new Category("Test_Products2","Test_Products2","a pic of various products.jpg");
        var savedCategory = categoryRepository.save(category);
        var brand = new Brand("Test_Brand2","puma_logo.png",new HashSet<>());
        brand.getCategories().add(savedCategory);
        var savedBrand = brandRepository.save(brand);

        Product product = new Product();
        product.setName("Iphone 17");
        product.setAlias("Iphone 17");
        product.setShortDescription("Latest Iphone from Apple with improved specs.");
        product.setFullDescription("Tim Cook talking about this phone");

        product.setBrand(savedBrand);
        product.setCategory(savedCategory);

        product.setPrice(200000);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        product.setMainImage("main iPhone 17 image");
        product.getImages().add(new ProductImage("rear picture",product));
        product.getImages().add(new ProductImage("back picture",product));
        product.getImages().add(new ProductImage("photos picture",product));

        var savedProduct = productRepository.save(product);

        assertThat(savedProduct.getName()).isEqualTo("Iphone 17");
        assertThat(savedProduct.getAlias()).isEqualTo("Iphone 17");
        assertThat(savedProduct.getBrand()).isEqualTo(savedBrand);
        assertThat(savedProduct.getCategory()).isEqualTo( savedCategory);
        assertThat(savedProduct.getImages().size()).isEqualTo( 3);
    }

    @Test
    public void whenPersistingProductProductDetails_productDetailsArePersistedInDB(){
        var category = new Category("Test_Products5","Test_Product5","a pic of various products.jpg");
        var savedCategory = categoryRepository.save(category);
        var brand = new Brand("Test_Brand5","puma_logo.png",new HashSet<>());
        brand.getCategories().add(savedCategory);
        var savedBrand = brandRepository.save(brand);

        Product product = new Product();
        product.setName("Iphone 18");
        product.setAlias("Iphone 18");
        product.setShortDescription("Latest Iphone from Apple with improved specs.");
        product.setFullDescription("Tim Cook talking about this phone");

        product.setBrand(savedBrand);
        product.setCategory(savedCategory);

        product.setPrice(200000);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        product.setMainImage("main iPhone 17 image");
        product.addNewProductDetail("CPU Speed","1.20 Ghz");
        product.addNewProductDetail("RAM","32 Gb");
        product.addNewProductDetail("HDD","SSD 2TB");


        var savedProduct = productRepository.save(product);

        assertThat(savedProduct.getName()).isEqualTo("Iphone 18");
        assertThat(savedProduct.getAlias()).isEqualTo("Iphone 18");
        assertThat(savedProduct.getBrand()).isEqualTo(savedBrand);
        assertThat(savedProduct.getCategory()).isEqualTo( savedCategory);
        assertThat(savedProduct.getDetails().size()).isEqualTo( 3);
    }
}
