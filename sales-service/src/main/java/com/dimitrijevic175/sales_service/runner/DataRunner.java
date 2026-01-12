package com.dimitrijevic175.sales_service.runner;

import com.dimitrijevic175.sales_service.domain.*;
import com.dimitrijevic175.sales_service.repository.CustomerRepository;
import com.dimitrijevic175.sales_service.repository.SalesOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataRunner implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final SalesOrderRepository salesOrderRepository;

    @Override
    public void run(String... args) {

        // =========================
        // CUSTOMER (firma)
        // =========================
        Customer companyCustomer = new Customer();
        companyCustomer.setCustomerType(CustomerType.COMPANY);
        companyCustomer.setCompanyName("ABC DOO");
        companyCustomer.setContactPerson("Petar Petrović");
        companyCustomer.setEmail("kontakt@abcdoo.rs");
        companyCustomer.setPhone("+38164111222");
        companyCustomer.setAddress("Bulevar Kralja Aleksandra 100, Beograd");
        companyCustomer.setTaxNumber("109876543");
        companyCustomer.setActive(true);

        customerRepository.save(companyCustomer);

        // =========================
        // CUSTOMER (fizičko lice)
        // =========================
        Customer privateCustomer = new Customer();
        privateCustomer.setCustomerType(CustomerType.INDIVIDUAL);
        privateCustomer.setFirstName("Marko");
        privateCustomer.setLastName("Marković");
        privateCustomer.setEmail("marko.markovic@gmail.com");
        privateCustomer.setPhone("+38163123456");
        privateCustomer.setAddress("Nemanjina 5, Novi Sad");
        privateCustomer.setActive(true);

        customerRepository.save(privateCustomer);

        // =========================
        // SALES ORDER
        // =========================
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setWarehouseId(1L);
        salesOrder.setCustomer(companyCustomer);
        salesOrder.setStatus(SalesOrderStatus.CREATED);

        // =========================
        // SALES ORDER ITEMS
        // =========================
        SalesOrderItem item1 = new SalesOrderItem();
        item1.setSalesOrder(salesOrder);
        item1.setProductId(1001L);
        item1.setQuantity(10);
        item1.setDiscount(new BigDecimal("5.00"));
        item1.setTaxRate(new BigDecimal("20.00"));
        item1.setSellingPrice(new BigDecimal("1200.00"));

        SalesOrderItem item2 = new SalesOrderItem();
        item2.setSalesOrder(salesOrder);
        item2.setProductId(1002L);
        item2.setQuantity(3);
        item2.setDiscount(BigDecimal.ZERO);
        item2.setTaxRate(new BigDecimal("20.00"));
        item2.setSellingPrice(new BigDecimal("4500.00"));

        salesOrder.setItems(List.of(item1, item2));

        salesOrderRepository.save(salesOrder);

        // =========================
        // STATUS PROMENA (primer)
        // =========================
        salesOrder.setStatus(SalesOrderStatus.CREATED);

        salesOrderRepository.save(salesOrder);
    }
}
