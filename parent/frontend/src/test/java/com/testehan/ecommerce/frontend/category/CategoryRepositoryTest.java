package com.testehan.ecommerce.frontend.category;

import com.testehan.ecommerce.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void whenFindAllEnabled_allEnabledCategoriesAreRetrieved(){
        Category category = new Category("FrontendCategory","FrontendCategory","a pic of various products.jpg");
        category.setEnabled(true);
        Category savedCategory = categoryRepository.save(category);

        assertThat(categoryRepository.findAllEnabled().contains(savedCategory)).isTrue();

    }

    @Test
    public void whenFindEnabledByAlias_enabledCategoryWithAliasIsRetrieved(){
        var category = new Category("FrontendCategory","FrontendCategory","a pic of various products.jpg");
        category.setEnabled(true);
        var savedCategory = categoryRepository.save(category);

        var foundCategory = categoryRepository.findByAliasEnabled("FrontendCategory");
        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getAlias()).isEqualTo("FrontendCategory");
        assertThat(foundCategory.getName()).isEqualTo("FrontendCategory");
    }
}
