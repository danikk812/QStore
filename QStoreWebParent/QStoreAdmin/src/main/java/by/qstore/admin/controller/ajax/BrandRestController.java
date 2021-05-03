package by.qstore.admin.controller.ajax;

import by.qstore.admin.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {

    @Autowired
    private BrandService brandService;

    @PostMapping("/brands/unique_check")
    public String checkUniqueBrand(Long id, String name) {
        return brandService.isBrandUnique(id, name);
    }
}
