package by.qstore.admin.repository;

import by.qstore.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public Product findByName(String name);

    @Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
    @Modifying
    public void updateEnabledStatus(Long id, boolean enabled);

    public Long countById(Long id);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %?1% "
            + "OR p.description LIKE %?1% "
            + "OR p.brand.name LIKE %?1% "
            + "OR p.category.name LIKE %?1%")
    public Page<Product> findAllByKeyword(String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = ?1")
    public Page<Product> findAllInCategory(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.category.id = ?1) AND "
            + "(p.name LIKE %?2% "
            + "OR p.description LIKE %?2% "
            + "OR p.brand.name LIKE %?2% "
            + "OR p.category.name LIKE %?2%)")
    public Page<Product> searchInCategory(Long categoryId, String keyword, Pageable pageable);
}
