package com.dimitrijevic175.sales_service.repository;

import com.dimitrijevic175.sales_service.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
