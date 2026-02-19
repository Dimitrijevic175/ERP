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

        if (customerRepository.count() > 0) return;

        // ================= CUSTOMERS =================

        Customer company1 = createCompany(
                "ABC DOO",
                "Petar Petrović",
                "kontakt@abcdoo.rs",
                "+38164111222",
                "Bulevar Kralja Aleksandra 100, Beograd",
                "109876543"
        );

        Customer individual1 = createIndividual(
                "Marko",
                "Marković",
                "marko.markovic@gmail.com",
                "+38163123456",
                "Nemanjina 5, Novi Sad"
        );

        Customer company2 = createCompany(
                "Gradnja Invest DOO",
                "Milan Ilić",
                "info@gradnjainvest.rs",
                "+381641223344",
                "Cara Dušana 12, Niš",
                "112233445"
        );

        Customer individual2 = createIndividual(
                "Jovan",
                "Jovanović",
                "jovan@gmail.com",
                "+38163888777",
                "Industrijska 7, Kragujevac"
        );

        // ================= SALES ORDERS (12) =================

        createOrder(1L, company1, SalesOrderStatus.CREATED, 1L, 20, 6.50);
        createOrder(1L, company1, SalesOrderStatus.CREATED, 2L, 10, 7.00);
        createOrder(2L, individual1, SalesOrderStatus.CLOSED, 3L, 5, 8.50);
        createOrder(2L, individual1, SalesOrderStatus.CLOSED, 4L, 3, 30.00);
        createOrder(3L, company2, SalesOrderStatus.CREATED, 5L, 2, 28.00);
        createOrder(3L, company2, SalesOrderStatus.CLOSED, 6L, 4, 35.00);
        createOrder(1L, individual2, SalesOrderStatus.CLOSED, 7L, 1000, 0.50);
        createOrder(2L, company1, SalesOrderStatus.CREATED, 8L, 500, 0.65);
        createOrder(1L, company2, SalesOrderStatus.CREATED, 9L, 150, 1.80);
        createOrder(3L, individual1, SalesOrderStatus.CREATED, 10L, 200, 0.90);
        createOrder(2L, company1, SalesOrderStatus.CLOSED, 11L, 300, 1.60);
        createOrder(1L, individual2, SalesOrderStatus.CREATED, 12L, 50, 22.00);

        System.out.println("12 Sales Orders i 4 Customer-a uspešno ubačeni!");
    }

    // ================= HELPER METODE =================

    private Customer createCompany(String companyName,
                                   String contactPerson,
                                   String email,
                                   String phone,
                                   String address,
                                   String taxNumber) {

        Customer c = new Customer();
        c.setCustomerType(CustomerType.COMPANY);
        c.setCompanyName(companyName);
        c.setContactPerson(contactPerson);
        c.setEmail(email);
        c.setPhone(phone);
        c.setAddress(address);
        c.setTaxNumber(taxNumber);
        c.setActive(true);

        return customerRepository.save(c);
    }

    private Customer createIndividual(String firstName,
                                      String lastName,
                                      String email,
                                      String phone,
                                      String address) {

        Customer c = new Customer();
        c.setCustomerType(CustomerType.INDIVIDUAL);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmail(email);
        c.setPhone(phone);
        c.setAddress(address);
        c.setActive(true);

        return customerRepository.save(c);
    }

    private void createOrder(Long warehouseId,
                             Customer customer,
                             SalesOrderStatus status,
                             Long productId,
                             int quantity,
                             double sellingPrice) {

        SalesOrder order = new SalesOrder();
        order.setWarehouseId(warehouseId);
        order.setCustomer(customer);
        order.setStatus(status);
        order.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 20)));

        SalesOrderItem item = new SalesOrderItem();
        item.setSalesOrder(order);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setDiscount(BigDecimal.ZERO);
        item.setTaxRate(new BigDecimal("20.00"));
        item.setSellingPrice(BigDecimal.valueOf(sellingPrice));

        order.setItems(List.of(item));

        salesOrderRepository.save(order);
    }
}
