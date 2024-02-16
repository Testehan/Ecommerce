package com.testehan.ecommerce.frontend.product;

import com.testehan.ecommerce.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void whenFindByAlias_productWithAliasIsReturned(){
        Product product = new Product();
        product.setName("Iphone 177");
        product.setAlias("Iphone-177");
        product.setShortDescription("Latest Iphone from Apple with improved specs.");
        product.setFullDescription("Tim Cook talking about this phone");
        product.setMainImage("blabla");

        var savedProduct = productRepository.save(product);

        assertThat(productRepository.findByAlias("Iphone-177")).isEqualTo(savedProduct);
    }
}
