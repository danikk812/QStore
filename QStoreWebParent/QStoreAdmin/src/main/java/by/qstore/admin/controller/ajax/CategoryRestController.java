package by.qstore.admin.controller.ajax;

import by.qstore.admin.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/categories/unique_check")
    public String checkUniqueCategory(Long id, String name, String alias) {
        return categoryService.isCategoryUnique(id, name, alias);
    }
}
