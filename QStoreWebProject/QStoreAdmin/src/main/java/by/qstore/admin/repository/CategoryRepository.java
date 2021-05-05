package by.qstore.admin.repository;

import by.qstore.common.entity.Category;
import by.qstore.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public Category findByName(String name);

    public Category findByAlias(String alias);

    public Long countById(Long id);

    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    @Modifying
    public void updateEnabledStatus(Long id, boolean enabled);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
    public Page<Category> findAllByKeyword(String  keyword, Pageable pageable);
}
