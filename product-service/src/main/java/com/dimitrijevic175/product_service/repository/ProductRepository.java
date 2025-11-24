package com.dimitrijevic175.product_service.repository;

import com.dimitrijevic175.product_service.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository  extends JpaRepository<Product, Long> {

}
