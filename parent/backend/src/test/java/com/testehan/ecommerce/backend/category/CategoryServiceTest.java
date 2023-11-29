package com.testehan.ecommerce.backend.category;

import com.testehan.ecommerce.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void whenCheckUniqueInNewModeWithDuplicateName_returnDuplicateName(){
        Integer id = null;
        var name="Computers";
        var alias = "abc";

        Category category = new Category(id,name,alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(category);

        var result = categoryService.checkUnique(id,name,alias);
        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void whenCheckUniqueInNewModeWithDuplicateAlias_returnDuplicateAlias(){
        Integer id = null;
        var name="Computers";
        var alias = "abc";

        Category category = new Category(id,name,alias);

        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        var result = categoryService.checkUnique(id,name,alias);
        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void whenCheckUniqueInNewModeWithoutDuplicateAliasOrName_returnOK(){
        Integer id = null;
        var name="Computers";
        var alias = "abc";

        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);
        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);

        var result = categoryService.checkUnique(id,name,alias);
        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void whenCheckUniqueInEditModeWithDuplicateName_returnDuplicateName(){
        Integer id = 1;
        var name="Computers";
        var alias = "abc";

        Category category = new Category(2,name,alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(category);

        var result = categoryService.checkUnique(id,name,alias);
        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void whenCheckUniqueInEditModeWithDuplicateAlias_returnDuplicateAlias(){
        Integer id = 1;
        var name="Computers";
        var alias = "abc";

        Category category = new Category(2,name,alias);

        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        var result = categoryService.checkUnique(id,name,alias);
        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void whenCheckUniqueInEditModeWithoutDuplicateAliasOrName_returnOK(){
        Integer id = 1;
        var name="Computers";
        var alias = "abc";

        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);
        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);

        var result = categoryService.checkUnique(id,name,alias);
        assertThat(result).isEqualTo("OK");
    }
}
