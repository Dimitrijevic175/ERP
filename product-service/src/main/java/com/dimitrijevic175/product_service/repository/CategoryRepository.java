package com.dimitrijevic175.product_service.repository;

import com.dimitrijevic175.product_service.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Long> {

}
