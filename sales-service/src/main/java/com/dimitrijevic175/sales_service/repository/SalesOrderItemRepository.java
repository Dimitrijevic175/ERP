package com.dimitrijevic175.sales_service.repository;

import com.dimitrijevic175.sales_service.domain.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

}
