package com.testehan.ecommerce.backend.category;

import com.testehan.ecommerce.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
// obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void whenRootCategoryIsCreated_categoryIsCreatedWithSuccess(){
        Category category = new Category("Products","Products","a pic of various products.jpg");

        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getName()).isEqualTo(category.getName());
        assertThat(savedCategory.getAlias()).isEqualTo(category.getAlias());
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void whenCategoryWithSubCategoriesIsCreated_categoryIsCreatedWithSuccess(){
        Category category = new Category("Products","Products","a pic of various products.jpg");
        Category savedCategory = categoryRepository.save(category);

        Category electronics = new Category("Electronics","Electronics","picture of various electronics.jpg");
        electronics.setParent(savedCategory);
        Category savedElectronics = categoryRepository.save(electronics);

        Category computer = new Category("Computer","Computer","picture of Computer.jpg");
        computer.setParent(savedElectronics);
        Category savedComputer = categoryRepository.save(computer);

        savedCategory = categoryRepository.findById(savedCategory.getId()).get();

        assertThat(savedCategory.getParent()).isNull();
        assertThat(savedElectronics.getParent()).isEqualTo(savedCategory);
        assertThat(savedComputer.getParent()).isEqualTo(savedElectronics);
    }

    @Test
    public void whenCategoryWithSameSubCategoriesIsCreated_categoryIsCreatedWithSuccess (){
        Category electronics = new Category("Electronics","Electronics","picture of various electronics.jpg");

        Category subCategory1 = new Category("Cameras", "Cameras","Cameras");
        subCategory1.setParent(electronics);
        Category subCategory2 = new Category("Smartphones", "Smartphones","Smartphones");
        subCategory2.setParent(electronics);

        Category subCategory21 = new Category("Apple", "Apple","Apple");
        subCategory21.setParent(subCategory2);
        Category subCategory22 = new Category("Android", "Android","Android");
        subCategory22.setParent(subCategory2);
        subCategory2.getChildren().addAll(List.of(subCategory21,subCategory22));

        electronics.getChildren().add(subCategory1);
        electronics.getChildren().add(subCategory2);

        final Category savedElectronics = categoryRepository.save(electronics);
        assertThat(
                savedElectronics.getChildren()
                        .stream()
                        .filter(child -> child.getParent().getId()==savedElectronics.getId())
                        .count())
                .isEqualTo(2);

    }

    @Test
    public void whenRootCategoriesAreObtainedFromDB_rootCategoriesAreRetrieveddWithSuccess(){
        Category electronics = new Category("Electronics2","Electronics2","picture of various electronics.jpg");
        Category furniture = new Category("Furniture2","Furniture2","picture of various Furniture.jpg");

        categoryRepository.save(electronics);
        categoryRepository.save(furniture);

        assertThat(categoryRepository.listRootCategories(Sort.by("name").ascending()).size()).isEqualTo(2);

    }

    @Test
    public void whenFindByName_categoryWithNameIsReturned(){
        Category electronics = new Category("Electronics2","Electronics2","picture of various electronics.jpg");

        categoryRepository.save(electronics);

        Category foundElectronics = categoryRepository.findByName("Electronics2");

        assertThat(foundElectronics.getName()).isEqualTo("Electronics2");
    }

    @Test
    public void whenFindByAlias_categoryWithAliasIsReturned(){
        Category electronics = new Category("Electronics2","Electronics2","picture of various electronics.jpg");

        categoryRepository.save(electronics);

        Category foundElectronics = categoryRepository.findByAlias("Electronics2");

        assertThat(foundElectronics.getAlias()).isEqualTo("Electronics2");
    }

    @Test
    public void testPrintCategoriesHierarchical() {
        Iterable<Category> categories = categoryRepository.findAll();

        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();
                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren(Category parent, int subLevel) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for (Category subCategory : children) {
            for (int i = 0; i < newSubLevel; i++) {
                System.out.print("--");
            }

            System.out.println(subCategory.getName());

            printChildren(subCategory, newSubLevel);
        }
    }


}
