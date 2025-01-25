package com.example.sfera_education.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.sfera_education.entity.DayOfWeek;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.EDayOfWeek;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.repository.DayOfWeekRepository;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DayOfWeekRepository dayOfWeekRepository;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args) {
        if (ddl.equals("create-drop") || ddl.equals("create")) {
            User newUser = new User();
            newUser.setFirstname("Admin");
            newUser.setLastname("Admin");
            newUser.setPassword(passwordEncoder.encode("root123"));
            newUser.setRole(ERole.ROLE_ADMIN);
            newUser.setPhoneNumber("998901234567");
            newUser.setEnabled(true);
            newUser.setAccountNonExpired(true);
            newUser.setAccountNonLocked(true);
            newUser.setCredentialsNonExpired(true);
            userRepository.save(newUser);

            DayOfWeek monday = new DayOfWeek();
            monday.setDayOfWeek(EDayOfWeek.MONDAY);
            dayOfWeekRepository.save(monday);
            DayOfWeek tuesday = new DayOfWeek();
            tuesday.setDayOfWeek(EDayOfWeek.TUESDAY);
            dayOfWeekRepository.save(tuesday);
            DayOfWeek wednesday = new DayOfWeek();
            wednesday.setDayOfWeek(EDayOfWeek.WEDNESDAY);
            dayOfWeekRepository.save(wednesday);
            DayOfWeek thursday = new DayOfWeek();
            thursday.setDayOfWeek(EDayOfWeek.THURSDAY);
            dayOfWeekRepository.save(thursday);
            DayOfWeek friday = new DayOfWeek();
            friday.setDayOfWeek(EDayOfWeek.FRIDAY);
            dayOfWeekRepository.save(friday);
            DayOfWeek saturday = new DayOfWeek();
            saturday.setDayOfWeek(EDayOfWeek.SATURDAY);
            dayOfWeekRepository.save(saturday);
            DayOfWeek sunday = new DayOfWeek();
            sunday.setDayOfWeek(EDayOfWeek.SUNDAY);
            dayOfWeekRepository.save(sunday);
        }
    }
}
