package by.qstore.admin.service;

import by.qstore.admin.exception.BrandNotFoundException;
import by.qstore.admin.exception.ProductNotFoundException;
import by.qstore.admin.repository.ProductRepository;
import by.qstore.common.entity.Brand;
import by.qstore.common.entity.Product;
import by.qstore.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 5;

    @Autowired
   private ProductRepository productRepository;

    public List<Product> listAll() {
        return  productRepository.findAll();
    }

    public Page<Product> listByPage(int pageNum, String sortBy, String sortOrder, String keyword, Long categoryId) {
        Sort sort = Sort.by(sortBy);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);

        if (keyword  != null && !keyword.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                return productRepository.searchInCategory(categoryId, keyword, pageable);
            }

        return productRepository.findAll(pageable);

        }

        if (categoryId != null && categoryId > 0) {
            return productRepository.findAllInCategory(categoryId, pageable);
        }

        return productRepository.findAll(pageable);
    }



    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(LocalDateTime.now());
        }
        
        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().toLowerCase().replaceAll(" ", "-");
            product.setAlias(defaultAlias);
        } else {
           product.setAlias(product.getAlias().toLowerCase().replaceAll(" ", "-"));
        }

        product.setUpdatedTime(LocalDateTime.now());

        return productRepository.save(product);
    }

    public String isProductUnique(Long id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = productRepository.findByName(name);

        if (isCreatingNew) {
            if (productByName != null) {
                return "existing";
            }
        } else {
            if (productByName != null && productByName.getId() != id) {
                return "existing";
            }
        }

        return "unique";
    }

    public void updateProductEnabledStatus(Long id, boolean enabled) {
        productRepository.updateEnabledStatus(id, enabled);
    }

    public void delete(Long id) throws ProductNotFoundException {
        Long countById = productRepository.countById(id);

        if (countById == null || countById == 0) {
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }

        productRepository.deleteById(id);
    }

    public Product getById(Long id) throws ProductNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
    }
}
