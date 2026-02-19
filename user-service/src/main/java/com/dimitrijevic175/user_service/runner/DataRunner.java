package com.dimitrijevic175.user_service.runner;

import com.dimitrijevic175.user_service.configuration.PasswordUtil;
import com.dimitrijevic175.user_service.domain.Role;
import com.dimitrijevic175.user_service.domain.RoleName;
import com.dimitrijevic175.user_service.domain.User;
import com.dimitrijevic175.user_service.repository.RoleRepository;
import com.dimitrijevic175.user_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void run() {

        if (userRepository.count() > 0) return;

        // ================= ROLES =================

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.ADMIN)));

        Role warehouseRole = roleRepository.findByName(RoleName.WAREHOUSE)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.WAREHOUSE)));

        Role procurementRole = roleRepository.findByName(RoleName.PROCUREMENT)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.PROCUREMENT)));

        Role salesRole = roleRepository.findByName(RoleName.SALES)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.SALES)));

        Role productRole = roleRepository.findByName(RoleName.PRODUCT)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.PRODUCT)));

        // ================= USERS (15) =================
        String defaultPassword = PasswordUtil.hashPassword("Password123");

        // ===== ADMIN (2) =====
        createUser("admin@gmail.com", "Admin", "Adminović", adminRole, defaultPassword);
        createUser("superadmin@gmail.com", "Milan", "Administrator", adminRole, defaultPassword);

        // ===== WAREHOUSE (3) =====
        createUser("warehouse1@gmail.com", "Nikola", "Skladištar", warehouseRole, defaultPassword);
        createUser("warehouse2@gmail.com", "Marko", "Magacioner", warehouseRole, defaultPassword);
        createUser("warehouse3@gmail.com", "Jovan", "Robni", warehouseRole, defaultPassword);

        // ===== PROCUREMENT (3) =====
        createUser("procurement1@gmail.com", "Ivana", "Nabavka", procurementRole, defaultPassword);
        createUser("procurement2@gmail.com", "Petar", "Kupac", procurementRole, defaultPassword);
        createUser("procurement3@gmail.com", "Ana", "Dobavljač", procurementRole, defaultPassword);

        // ===== SALES (4) =====
        createUser("sales1@gmail.com", "Luka", "Prodavac", salesRole, defaultPassword);
        createUser("sales2@gmail.com", "Stefan", "Komercijalista", salesRole, defaultPassword);
        createUser("sales3@gmail.com", "Milica", "Prodaja", salesRole, defaultPassword);
        createUser("sales4@gmail.com", "Teodora", "Fakturisanje", salesRole, defaultPassword);

        // ===== PRODUCT (3) =====
        createUser("product1@gmail.com", "Vladimir", "Katalog", productRole, defaultPassword);
        createUser("product2@gmail.com", "Nemanja", "Artikal", productRole, defaultPassword);
        createUser("product3@gmail.com", "Jelena", "Specifikacija", productRole, defaultPassword);

        System.out.println("15 korisnika uspešno ubačeno u bazu!");
    }

    private void createUser(String email,
                            String firstName,
                            String lastName,
                            Role role,
                            String hashedPassword) {

        userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setEmail(email);
            user.setPassword(hashedPassword);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(role);
            user.setActive(true);
            return userRepository.save(user);
        });
    }
}
