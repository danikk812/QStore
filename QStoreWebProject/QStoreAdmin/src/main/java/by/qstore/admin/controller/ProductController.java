package by.qstore.admin.controller;

import by.qstore.admin.exception.BrandNotFoundException;
import by.qstore.admin.exception.ProductNotFoundException;
import by.qstore.admin.service.BrandService;
import by.qstore.admin.service.CategoryService;
import by.qstore.admin.service.ProductService;
import by.qstore.admin.service.UserService;
import by.qstore.admin.util.FileUtil;
import by.qstore.common.entity.Brand;
import by.qstore.common.entity.Category;
import by.qstore.common.entity.Product;
import by.qstore.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public ModelAndView listFirstPage(ModelAndView modelAndView) {
        return listByPage(1, "name", "asc", null, 0L,  modelAndView);
    }

    @GetMapping("products/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable("pageNum") int pageNum, @RequestParam("sortBy") String sortBy,
                                   @RequestParam("sortOrder") String sortOrder, @RequestParam("keyword") String keyword,
                                   @RequestParam("categoryId") Long categoryId, ModelAndView modelAndView) {
        Page<Product> page = productService.listByPage(pageNum, sortBy, sortOrder, keyword, categoryId);
        List<Product> productList = page.getContent();

        List<Category> categoryList = categoryService.listAll();


        long startCount = getPageStartCount(pageNum);
        long endCount = getPageEndCount(pageNum, page);

        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";
        
        if (categoryId != null) {
            modelAndView.addObject("categoryId", categoryId);
        }

        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.addObject("currentPage", pageNum);
        modelAndView.addObject("startCount", startCount);
        modelAndView.addObject("endCount", endCount);
        modelAndView.addObject("totalItems", page.getTotalElements());


        modelAndView.addObject("sortBy", sortBy);
        modelAndView.addObject("sortOrder", sortOrder);
        modelAndView.addObject("reverseSortOrder", reverseSortOrder);

        modelAndView.addObject("keyword", keyword);

        modelAndView.addObject("productList", productList);
        modelAndView.addObject("categoryList", categoryList);

        modelAndView.setViewName("product/products");
        return modelAndView;
    }

    @GetMapping("/products/new")
    public ModelAndView addProduct(ModelAndView modelAndView) {
        List<Brand> brandList = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        modelAndView.addObject("productForm", product);
        modelAndView.addObject("brandList", brandList);
        modelAndView.addObject("pageTitle", "Create New Product");

        modelAndView.setViewName("product/product_form");
        return modelAndView;
    }

    @PostMapping("/products/save")
    public ModelAndView saveProduct(@ModelAttribute("productForm") Product product, @RequestParam("fileImage") MultipartFile multipartFile,
                                    RedirectAttributes redirectAttributes, ModelAndView modelAndView) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setMainImage(fileName);

            Product savedProduct = productService.save(product);
            String uploadDir = "../product-images/" + savedProduct.getId();

            FileUtil.cleanDir(uploadDir);
            FileUtil.saveFile(uploadDir, fileName, multipartFile);

        } else {
            productService.save(product);
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");

        modelAndView.setViewName("redirect:/products");
        return modelAndView;
    }

    @GetMapping("/products/{id}/enabled/{enabledStatus}")
    public ModelAndView updateCategoryEnabledStatus(@PathVariable("id") Long id, @PathVariable("enabledStatus") boolean enabled,
                                              RedirectAttributes redirectAttributes, ModelAndView modelAndView) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Product with id " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

       modelAndView.setViewName("redirect:/products");
       return modelAndView;
    }

    @GetMapping("/products/delete/{id}")
    public ModelAndView deleteProduct(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes,
                                    ModelAndView modelAndView) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The product with id " + id + " has been deleted successfully");
        } catch (ProductNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        modelAndView.setViewName("redirect:/products");
        return modelAndView;
    }

    @GetMapping("/products/edit/{id}")
    public ModelAndView editProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,
                              ModelAndView modelAndView) {
        try {
            Product product = productService.getById(id);
            List<Brand> brandList = brandService.listAll();


            modelAndView.addObject("productForm", product);
            modelAndView.addObject("brandList", brandList);
            modelAndView.addObject("pageTitle", "Edit Product #" + id);

            modelAndView.setViewName("product/product_form");
            return modelAndView;

        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());

            modelAndView.setViewName("redirect:/products");
            return modelAndView;
        }
    }



    private long getPageStartCount(int pageNum) {
        return (long) (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
    }

    private long getPageEndCount(int pageNum, Page<Product> page) {
        long endCount = getPageStartCount(pageNum) + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        return endCount;
    }

}


