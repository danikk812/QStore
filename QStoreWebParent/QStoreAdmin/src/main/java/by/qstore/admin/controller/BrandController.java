package by.qstore.admin.controller;

import by.qstore.admin.exception.BrandNotFoundException;
import by.qstore.admin.exception.CategoryNotFoundException;
import by.qstore.admin.service.BrandService;
import by.qstore.admin.service.CategoryService;
import by.qstore.admin.util.FileUtil;
import by.qstore.common.entity.Brand;
import by.qstore.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/brands")
    public ModelAndView listFirstPage(@RequestParam(name = "sortOrder", required = false) String sortOrder, ModelAndView modelAndView) {
        return listByPage(1, sortOrder, null, modelAndView);
    }

    @GetMapping("/brands/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable("pageNum") int pageNum, @RequestParam(name = "sortOrder") String sortOrder,
                                   @RequestParam("keyword") String keyword, ModelAndView modelAndView) {
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "asc";
        }
        Page<Brand> page = brandService.listByPage(pageNum, sortOrder, keyword);
        List<Brand> brandList = page.getContent();

        long startCount = getPageStartCount(pageNum);
        long endCount = getPageEndCount(pageNum, page);

        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.addObject("totalItems", page.getTotalElements());
        modelAndView.addObject("currentPage", pageNum);
        modelAndView.addObject("startCount", startCount);
        modelAndView.addObject("endCount", endCount);

        modelAndView.addObject("sortBy", "name");
        modelAndView.addObject("sortOrder", sortOrder);
        modelAndView.addObject("reverseSortOrder", reverseSortOrder);

        modelAndView.addObject("keyword", keyword);

        modelAndView.addObject("brandList", brandList);

        modelAndView.setViewName("brand/brands");
        return modelAndView;

    }

    @GetMapping("/brands/new")
    public ModelAndView addBrand(ModelAndView modelAndView) {
        List<Category> categoryList = categoryService.listAll();

        modelAndView.addObject("brandForm", new Brand());
        modelAndView.addObject("categoryList", categoryList);
        modelAndView.addObject("pageTitle", "Create New Brand");

        modelAndView.setViewName("brand/brand_form");
        return modelAndView;
    }

    @PostMapping("/brands/save")
    public ModelAndView saveCategory(@ModelAttribute("brandForm") Brand brand, RedirectAttributes redirectAttributes,
                                     ModelAndView modelAndView) throws IOException {
        brandService.save(brand);

        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully");
        modelAndView.setViewName("redirect:/brands");
        return modelAndView;
    }

    @GetMapping("/brands/edit/{id}")
    public ModelAndView editCategory(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes,
                                     ModelAndView modelAndView) {
        try {
            Brand brandById = brandService.getById(id);
            List<Category> categoryList = categoryService.listAll();


            modelAndView.addObject("brandForm", brandById);
            modelAndView.addObject("categoryList", categoryList);
            modelAndView.addObject("pageTitle", "Edit Brand #" + id);

            modelAndView.setViewName("brand/brand_form");
            return modelAndView;
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            modelAndView.setViewName("redirect:/brands");
            return modelAndView;
        }
    }

    @GetMapping("/brands/delete/{id}")
    public ModelAndView deleteUser(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes,
                                   ModelAndView modelAndView) {
        try {
            brandService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The brand with id " + id + " has been deleted successfully");
        } catch (BrandNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        modelAndView.setViewName("redirect:/brands");
        return modelAndView;
    }



    private long getPageStartCount(int pageNum) {
        return (long) (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
    }

    private long getPageEndCount(int pageNum, Page<Brand> page) {
        long endCount = getPageStartCount(pageNum) + BrandService.BRANDS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        return endCount;
    }
}
