package bd.edu.seu.pulse;

import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class PulseApplication {

    private static final Logger logger = LoggerFactory.getLogger(PulseApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PulseApplication.class, args);
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            try {
                if (!userRepository.existsByUsername("admin")) {
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setPassword(passwordEncoder.encode("admin123"));
                    admin.setName("System Admin");
                    admin.setRole("ADMIN");
                    admin.setEmail("admin@flicker.com");
                    admin.setAccountStatus("ACTIVE");
                    admin.setInterests(new ArrayList<>());
                    admin.setFollowingWriters(new ArrayList<>());
                    admin.setFollowers(new ArrayList<>());
                    userRepository.save(admin);
                    logger.info("Admin user created successfully");
                } else {
                    logger.info("Admin user already exists");
                }
            } catch (Exception e) {
                logger.error("Failed to initialize admin user. This may be due to MongoDB connection issues.", e);
                logger.warn("Application will continue, but admin user may not be available.");
            }
        };
    }
}
