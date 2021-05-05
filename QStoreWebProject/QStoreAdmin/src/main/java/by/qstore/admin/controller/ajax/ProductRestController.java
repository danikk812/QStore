package by.qstore.admin.controller.ajax;

import by.qstore.admin.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductRestController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products/unique_check")
    public String checkUniqueProduct(Long id, String name) {
        return productService.isProductUnique(id, name);
    }
}
