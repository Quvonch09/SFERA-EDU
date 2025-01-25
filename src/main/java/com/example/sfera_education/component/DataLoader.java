package com.example.sfera_education.component;

import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        if (ddl.equals("create-drop") || ddl.equals("create")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Admin");
            admin.setPassword(passwordEncoder.encode("12345"));
            admin.setRole(ERole.ROLE_ADMIN);
            admin.setPhoneNumber("998993393300");
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            userRepository.save(admin);

            loadFunctions();
        }
    }


    public void loadFunctions()
    {
        try {
            ClassPathResource classPathResource = new ClassPathResource("dayOfWeek.sql");
            try (InputStream inputStream = classPathResource.getInputStream()) {
                String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                jdbcTemplate.execute(sql);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
