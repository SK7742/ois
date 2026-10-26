package com.oxford.csms.config;

import com.oxford.csms.domain.finance.model.FeeCategory;
import com.oxford.csms.domain.finance.model.FeeStructure;
import com.oxford.csms.domain.finance.repository.FeeStructureRepository;
import com.oxford.csms.security.Role;
import com.oxford.csms.security.User;
import com.oxford.csms.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FeeStructureRepository feeStructureRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           FeeStructureRepository feeStructureRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.feeStructureRepository = feeStructureRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking institutional production environment configuration for Oxford International School...");

        // 1. Ensure Single Admin User Exists (Remove all non-admin accounts)
        userRepository.findAll().forEach(u -> {
            if (!"admin".equalsIgnoreCase(u.getUsername())) {
                log.info("Removing legacy non-admin user account: {}", u.getUsername());
                userRepository.delete(u);
            }
        });

        if (userRepository.findByUsernameIgnoreCase("admin").isEmpty()) {
            log.info("Creating primary institutional Administrator account: admin...");
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("Admin@123"),
                    "System Administrator",
                    "admin@oxford.edu",
                    "+91-7870335141",
                    Set.of(Role.ROLE_ADMIN, Role.ROLE_MANAGEMENT)
            );
            userRepository.save(admin);
            log.info("Primary administrator account successfully initialized.");
        } else {
            // Update admin password to ensure it's Admin@123
            userRepository.findByUsernameIgnoreCase("admin").ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_MANAGEMENT));
                userRepository.save(admin);
            });
        }

        // 2. Standard Institutional Fee Structures (Nursery to Class 10)
        if (feeStructureRepository.count() == 0) {
            log.info("Initializing baseline academic fee structures for Oxford International School...");
            String[] classes = {
                    "Nur.", "LKG", "UKG",
                    "Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
                    "Class 6", "Class 7", "Class 8", "Class 9", "Class 10"
            };

            for (String cls : classes) {
                BigDecimal tuition = new BigDecimal("4500.00");
                feeStructureRepository.save(new FeeStructure(cls, FeeCategory.TUITION, tuition, "MONTHLY", "Core Academic Tuition Fee"));
            }
            log.info("Standard fee structures successfully established for all 13 academic classes.");
        }

        log.info("Data initialization complete: Single administrator account active. Zero dummy data present.");
    }
}
