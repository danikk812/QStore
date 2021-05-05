package by.qstore.admin.service;

import by.qstore.admin.exception.BrandNotFoundException;
import by.qstore.admin.exception.CategoryNotFoundException;
import by.qstore.admin.repository.BrandRepository;
import by.qstore.common.entity.Brand;
import by.qstore.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    public static final int BRANDS_PER_PAGE = 7;

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listAll() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Page<Brand> listByPage(int pageNum, String sortOrder, String keyword) {
        Sort sort = Sort.by("name");

        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);

        if(keyword != null && !keyword.isEmpty()) {
            return brandRepository.findAllByKeyword(keyword, pageable);
        }

        return brandRepository.findAll(pageable);
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand getById(Long id) throws BrandNotFoundException {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Brand with id " + id + " not found"));
    }

    public void delete(Long id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);
        if (countById == null || countById == 0) {
            throw new BrandNotFoundException("Brand with id " + id + " not found");
        }
        brandRepository.deleteById(id);
    }

    public String isBrandUnique(Long id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Brand brandByName = brandRepository.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null)  {
                return "existing";
            }
        } else {
            if (brandByName != null && brandByName.getId() != id) {
                return "existing";
            }
        }

        return "unique";
    }

}
