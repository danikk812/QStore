package by.qstore.admin.controller.ajax;

import by.qstore.admin.entity.dto.CategoryDTO;
import by.qstore.admin.exception.BrandNotFoundException;
import by.qstore.admin.exception.BrandNotFoundRestException;
import by.qstore.admin.service.BrandService;
import by.qstore.common.entity.Brand;
import by.qstore.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/brands/unique_check")
    public String checkUniqueBrand(Long id, String name) {
        return brandService.isBrandUnique(id, name);
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name ="id") Long brandId) throws BrandNotFoundRestException {
        List<CategoryDTO> categoryList = new ArrayList<>();

        try {
            Brand brand = brandService.getById(brandId);
            Set<Category> categories = brand.getCategories();

            for (Category category : categories) {
                CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
                categoryList.add(dto);
            }

            return categoryList;
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
    }
}
