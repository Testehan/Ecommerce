package com.testehan.ecommerce.frontend.category;

import com.testehan.ecommerce.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
}
