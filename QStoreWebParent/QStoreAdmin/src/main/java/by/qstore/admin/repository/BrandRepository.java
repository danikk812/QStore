package by.qstore.admin.repository;

import by.qstore.common.entity.Brand;
import by.qstore.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    public Long countById(Long id);

    public Brand findByName(String name);

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    public Page<Brand> findAllByKeyword(String  keyword, Pageable pageable);
}
