package com.deliverx.auth.config;

import com.deliverx.auth.entity.User;
import com.deliverx.auth.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Создание тестового пользователя при запуске
 * Remove or gate behind a profile (e.g. @Profile("!prod")) before going live.
 */
@Profile("!prod")
@Component
public class DataInitializer implements ApplicationRunner {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUser("misha.petrov@mailtest.com", "SecurePass99");
    }

    private void seedUser(String email, String rawPassword) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User(email, passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            System.out.printf("[DataInitializer] Seeded user: %s%n %s%n", email, passwordEncoder.encode(rawPassword));
        }
    }
}
