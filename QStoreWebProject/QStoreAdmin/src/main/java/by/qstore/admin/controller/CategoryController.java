package by.qstore.admin.controller;

import by.qstore.admin.exception.CategoryNotFoundException;
import by.qstore.admin.export.CategoryExcelExporter;
import by.qstore.admin.export.UserExcelExporter;
import by.qstore.admin.service.CategoryService;
import by.qstore.admin.service.UserService;
import by.qstore.admin.util.FileUtil;
import by.qstore.common.entity.Category;
import by.qstore.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ModelAndView listFirstPage(@RequestParam(name = "sortOrder", required = false) String sortOrder, ModelAndView modelAndView) {
        return listByPage(1, sortOrder, null, modelAndView);
    }

    @GetMapping("/categories/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable("pageNum") int pageNum, @RequestParam(name = "sortOrder") String sortOrder,
                                   @RequestParam("keyword") String keyword, ModelAndView modelAndView) {
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "asc";
        }
        Page<Category> page = categoryService.listByPage(pageNum, sortOrder, keyword);
        List<Category> categoryList = page.getContent();

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

        modelAndView.addObject("categoryList", categoryList);

        modelAndView.setViewName("category/categories");
        return modelAndView;

    }

    @GetMapping("/categories/new")
    public ModelAndView addCategory(ModelAndView modelAndView) {
        modelAndView.addObject("categoryForm", new Category());
        modelAndView.addObject("pageTitle", "Create New Category");

        modelAndView.setViewName("category/category_form");
        return modelAndView;
    }

    @PostMapping("/categories/save")
    public ModelAndView saveCategory(@ModelAttribute("categoryForm") Category category,
                                     @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes redirectAttributes,
                                     ModelAndView modelAndView) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);
            Category savedCategory = categoryService.save(category);
            String uploadDir = "../category-images/" + savedCategory.getId();

            FileUtil.cleanDir(uploadDir);
            FileUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            categoryService.save(category);
        }

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");
        modelAndView.setViewName("redirect:/categories");
        return modelAndView;
    }

    @GetMapping("/categories/edit/{id}")
    public ModelAndView editCategory(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes,
                                 ModelAndView modelAndView) {
        try {
            Category categoryById = categoryService.getById(id);

            modelAndView.addObject("categoryForm", categoryById);
            modelAndView.addObject("pageTitle", "Edit Category #" + id);

            modelAndView.setViewName("category/category_form");
            return modelAndView;
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            modelAndView.setViewName("redirect:/categories");
            return modelAndView;
        }
    }

    @GetMapping("/categories/{id}/enabled/{enabledStatus}")
    public ModelAndView updateCategoryEnabledStatus(@PathVariable("id") Long id, @PathVariable("enabledStatus") boolean enabled,
                                                RedirectAttributes redirectAttributes, ModelAndView modelAndView) {
        categoryService.updateCategoryEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The category with id " + id + " has been " + status);
        modelAndView.setViewName("redirect:/categories");
        return modelAndView;
    }

    @GetMapping("/categories/delete/{id}")
    public ModelAndView deleteUser(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes,
                                   ModelAndView modelAndView) {
        try {
            categoryService.delete(id);
            String categoryDir = "../category-images/" + id;
            FileUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The category with id " + id + " has been deleted successfully");
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        modelAndView.setViewName("redirect:/categories");
        return modelAndView;
    }

    @GetMapping("/categories/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Category> categoryList = categoryService.listAll();

        CategoryExcelExporter exporter = new CategoryExcelExporter();
        exporter.export(categoryList, response);
    }



    private long getPageStartCount(int pageNum) {
        return (long) (pageNum - 1) * CategoryService.CATEGORIES_PER_PAGE + 1;
    }

    private long getPageEndCount(int pageNum, Page<Category> page) {
        long endCount = getPageStartCount(pageNum) + CategoryService.CATEGORIES_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        return endCount;
    }
}
