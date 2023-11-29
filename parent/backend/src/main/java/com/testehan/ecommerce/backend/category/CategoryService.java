package com.testehan.ecommerce.backend.category;

import com.testehan.ecommerce.common.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAllCategories(String sortDir){
        Sort sort = Sort.by("name");
         if (sortDir.equalsIgnoreCase("asc")){
            sort = sort.ascending();
        } else  if (sortDir.equalsIgnoreCase("desc")){
            sort = sort.descending();
        }
        return listHierarchicalCategories(categoryRepository.listRootCategories(sort), sortDir);
    }

    private List<Category> listHierarchicalCategories(List<Category> listRootCategories, String sortDir){
        List<Category> hierarchicalCategories = new ArrayList<>();

        for (Category category : listRootCategories){
            hierarchicalCategories.add(Category.copyPartial(category));

            Set<Category> childrenCategories = sortSubCategories(category.getChildren(), sortDir);
            for (Category subCategory : childrenCategories){
                String newName = "--" + subCategory.getName();
                hierarchicalCategories.add(Category.copyPartialWithNewName(subCategory, newName));

                listSubHierarchicalCategories(subCategory,1,hierarchicalCategories,sortDir);
            }
        }

        return hierarchicalCategories;
    }

    private void listSubHierarchicalCategories(Category parent, int subLevel, List<Category> hierarchicalCategories, String sortDir){
        Set<Category> childrenCategories = sortSubCategories(parent.getChildren(),sortDir);
        int newSubLevel = subLevel + 1;

        for (Category subCategory : childrenCategories) {
            StringBuilder newName = new StringBuilder();
            for (int i = 0; i < newSubLevel; i++) {
                newName.append("--");
            }
            newName.append(subCategory.getName());

            hierarchicalCategories.add(Category.copyPartialWithNewName(subCategory, newName.toString()));

            listSubHierarchicalCategories(subCategory,newSubLevel,hierarchicalCategories,sortDir);
        }
    }

    public List<Category> listCategoriesInForm(){
        List<Category> categoriesInForm = new ArrayList<>();

        Iterable<Category> categoriesInDB = categoryRepository.listRootCategories(Sort.by("name").ascending());
        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesInForm.add(new Category(category.getId(),category.getName()));

                Set<Category> children = sortSubCategories(category.getChildren(),"asc");
                for (Category subCategory : children) {
                    categoriesInForm.add(new Category(subCategory.getId(),"--" +subCategory.getName()));
                    addChildrenCategories(subCategory, 1, categoriesInForm);
                }
            }
        }

        return categoriesInForm;
    }

    private void addChildrenCategories(Category parent, int subLevel, List<Category> categoriesInForm) {
        int newSubLevel = subLevel + 1;
        Set<Category> children = sortSubCategories(parent.getChildren(),"asc");

        for (Category subCategory : children) {
            StringBuilder lines = new StringBuilder();
            for (int i = 0; i < newSubLevel; i++) {
                lines.append("--");
            }

            categoriesInForm.add(new Category(subCategory.getId(),lines + subCategory.getName()));

            addChildrenCategories(subCategory, newSubLevel, categoriesInForm);
        }
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category findById(Integer id) throws  CategoryNotFoundException{
        try {
            return categoryRepository.findById(id).get();
        } catch(NoSuchElementException e){
            throw new CategoryNotFoundException("Could not find any category with id " + id);
        }
    }

    public String checkUnique(Integer id, String name, String alias){
        boolean isCreatingNew = (id == null || id==0);

        var categoryByName = categoryRepository.findByName(name);
        var categoryByAlias = categoryRepository.findByAlias(alias);

        if (isCreatingNew){
            if (categoryByName != null){
                return "DuplicateName";
            }
            if (categoryByAlias != null){
                return "DuplicateAlias";
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id){
                return "DuplicateName";
            }
            if (categoryByAlias != null && categoryByAlias.getId() != id){
                return "DuplicateAlias";
            }
        }

        return "OK";
    }

    private SortedSet<Category> sortSubCategories(Set<Category> subCategories, String sortDir){
        SortedSet<Category> sortedSubCategories = new TreeSet<>((o1, o2) -> {
            if (sortDir.equalsIgnoreCase("asc")) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return o2.getName().compareTo(o1.getName());
            }
        });

        sortedSubCategories.addAll(subCategories);
        return sortedSubCategories;
    }

    public void updateEnabledStatus(Integer id, boolean enabled) {
        categoryRepository.updateEnabledStatus(id,enabled);
    }
}
