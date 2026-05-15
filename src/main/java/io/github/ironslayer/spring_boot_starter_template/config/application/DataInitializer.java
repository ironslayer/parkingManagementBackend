package io.github.ironslayer.spring_boot_starter_template.config.application;

import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.Role;
import io.github.ironslayer.spring_boot_starter_template.user.domain.entity.User;
import io.github.ironslayer.spring_boot_starter_template.user.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Se ejecuta antes que otros CommandLineRunner
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@parking.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Starting parking management system default data configuration...");
        createDefaultAdminUser();
        log.info("✅ Default data configuration completed successfully");
    }

    private void createDefaultAdminUser() {
        try {
            if (!userRepository.existsByEmail(adminEmail)) {
                User adminUser = User.builder()
                        .firstname("Admin")
                        .lastname("Sistema")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                userRepository.save(adminUser);

                log.info("✅ Default admin user created successfully:");
                log.info("   📧 Email: {}", adminEmail);
                log.info("   🔑 Password: {}", adminPassword);
                log.info("   👤 Role: ADMIN");
                log.info("   ⚠️  IMPORTANT: Change default password in production");
            } else {
                log.info("ℹ️  Default admin user already exists: {}", adminEmail);
            }
        } catch (Exception e) {
            log.error("❌ Error creating default admin user: {}", e.getMessage(), e);
            // No lanzamos la excepción para que la aplicación continúe
        }
    }
}
