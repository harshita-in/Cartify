package com.ecommerce.userservice;

import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed a default customer user if database is empty
            if (userRepository.findByUsername("customer").isEmpty()) {
                User customer = new User("customer", passwordEncoder.encode("password"), "ROLE_USER");
                userRepository.save(customer);
                System.out.println("Seeded default customer: customer/password");
            }
            // Seed a default admin user if database is empty
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", passwordEncoder.encode("password"), "ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println("Seeded default admin: admin/password");
            }
        };
    }
}
