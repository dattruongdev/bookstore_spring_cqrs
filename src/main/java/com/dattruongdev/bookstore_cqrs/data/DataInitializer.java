package com.dattruongdev.bookstore_cqrs.data;

import com.dattruongdev.bookstore_cqrs.core.auth.domain.Role;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.RoleRepository;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.User;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EventListener;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationListener<ApplicationStartedEvent> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        createRoleIfNotExist();
        createAdminIfNotExist();
    }

    private void createRoleIfNotExist() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            Role member = new Role();
            member.setRoleName("ROLE_USER");
            roleRepository.save(member);

            Role admin = new Role();
            admin.setRoleName("ROLE_ADMIN");
            roleRepository.save(admin);
        }
    }

    private void createAdminIfNotExist() {
        Role role = roleRepository.findByRoleName("ROLE_ADMIN");

        if (role == null) {
            role = new Role();
            role.setRoleName("ROLE_ADMIN");
            role = roleRepository.save(role);
        }

        User user = userRepository.findByUsername("admin");
        if (user == null) {
            user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRoles(List.of(role));
            userRepository.save(user);
        }
    }
}
