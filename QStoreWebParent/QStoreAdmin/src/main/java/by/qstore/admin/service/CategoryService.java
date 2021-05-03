package by.qstore.admin.service;

import by.qstore.admin.exception.CategoryNotFoundException;
import by.qstore.admin.repository.CategoryRepository;
import by.qstore.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    public static final int CATEGORIES_PER_PAGE = 4;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listAll() {
        return  categoryRepository.findAll();
    }

    public Page<Category> listByPage(int pageNum, String sortOrder, String keyword) {
        Sort sort = Sort.by("name");

        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE, sort);

        if(keyword != null && !keyword.isEmpty()) {
            return categoryRepository.findAllByKeyword(keyword, pageable);
        }

        return categoryRepository.findAll(pageable);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public Category getById(Long id) throws CategoryNotFoundException {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + id + " not found"));
    }

    public String isCategoryUnique(Long id, String name, String alias) {
        boolean isCreatingNew = (id == null);

        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) {
                return "existingName";
            } else {
                Category categoryByAlias = categoryRepository.findByAlias(alias);
                if (categoryByAlias != null) {
                    return "existingAlias";
                }
            }
        } else {
            if (categoryByName != null && categoryByName.getId() != id) {
                return "existingName";
            }

            Category categoryByAlias = categoryRepository.findByAlias(alias);
            if (categoryByAlias != null && categoryByAlias.getId() != id) {
                return "existingAlias";
            }
        }

        return "unique";
    }

    public void updateCategoryEnabledStatus(Long id, boolean enabled) {
        categoryRepository.updateEnabledStatus(id, enabled);
    }

    public void delete(Long id) throws CategoryNotFoundException {
        Long countById = categoryRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new CategoryNotFoundException("Category with id " + id + " not found");
        }
        categoryRepository.deleteById(id);
    }


}
