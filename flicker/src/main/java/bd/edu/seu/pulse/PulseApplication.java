package bd.edu.seu.pulse;

import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class PulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PulseApplication.class, args);
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
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
            }
        };
    }
}
