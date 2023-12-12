package com.testehan.ecommerce.frontend;

import com.testehan.ecommerce.frontend.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("")
	public String viewHomePage(Model model) {
		var listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories",listCategories);

		return "index";
	}
}