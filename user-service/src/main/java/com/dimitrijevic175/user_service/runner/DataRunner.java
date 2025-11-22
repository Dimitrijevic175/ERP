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


        userRepository.findByEmail("admin@gmail.com").orElseGet(() -> {
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(PasswordUtil.hashPassword("admin"));
            admin.setFirstName("Admin");
            admin.setLastName("Adminovic");
            admin.setRole(adminRole);
            admin.setActive(true);
            return userRepository.save(admin);
        });
    }
}
