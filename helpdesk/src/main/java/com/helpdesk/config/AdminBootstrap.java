package com.helpdesk.config;

import com.helpdesk.common.enums.Role;
import com.helpdesk.user.entity.User;
import com.helpdesk.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {
    @Bean
    CommandLineRunner createInitialAdmin(UserRepository users, PasswordEncoder encoder,
            @Value("${app.bootstrap-admin.name:}") String name,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password) {
        return args -> {
            if (name.isBlank() || email.isBlank() || password.isBlank()) return;
            if (password.length() < 12) throw new IllegalStateException("ADMIN_PASSWORD must contain at least 12 characters");
            String normalizedEmail = email.trim().toLowerCase();
            if (users.existsByEmail(normalizedEmail)) return;
            User admin = new User();
            admin.setName(name.trim());
            admin.setEmail(normalizedEmail);
            admin.setPassword(encoder.encode(password));
            admin.setRole(Role.ADMIN);
            users.save(admin);
        };
    }
}
