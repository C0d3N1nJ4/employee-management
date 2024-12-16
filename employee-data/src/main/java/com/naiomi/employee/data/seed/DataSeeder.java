package com.naiomi.employee.data.seed;

import com.naiomi.employee.data.constant.RoleType;
import com.naiomi.employee.data.model.Role;
import com.naiomi.employee.data.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            for (RoleType roleType : RoleType.values()) {
                roleRepository.save(new Role(roleType));
                System.out.println("Seeded Role: " + roleType);
            }
        } else {
            System.out.println("Roles already seeded.");
        }
    }

}
