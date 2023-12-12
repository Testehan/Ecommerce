package com.testehan.ecommerce.frontend.category;

import com.testehan.ecommerce.common.entity.Category;
import com.testehan.ecommerce.common.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories(){
        List<Category> noChildrenCategories = new ArrayList<>();
        var allEnabledCategories = categoryRepository.findAllEnabled();

        allEnabledCategories.stream().filter(c -> c.getChildren()==null || c.getChildren().size()==0)
                .forEach(c -> noChildrenCategories.add(c));

        return noChildrenCategories;
    }

    public Category getCategoryByAlias(String alias) throws CategoryNotFoundException {
        var category = categoryRepository.findByAliasEnabled(alias);
        if (Objects.isNull(category)){
            throw new CategoryNotFoundException("Category with alias " + alias + " was not found!");
        }

        return category;
    }

    public List<Category> getCategoryParents(Category child){
        List<Category> parentCategories = new ArrayList<>();

        var parent = child.getParent();
        while (parent != null){
            parentCategories.add(0,parent); // the biggest parent will always be on position 0..next one will be on 1 and so on
            parent = parent.getParent();
        }

        parentCategories.add(child);

        return parentCategories;
    }
}
