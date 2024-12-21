package com.dattruongdev.bookstore_cqrs.core.auth.command;

import com.dattruongdev.bookstore_cqrs.core.auth.domain.Role;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.RoleRepository;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.User;
import com.dattruongdev.bookstore_cqrs.core.auth.domain.UserRepository;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ApiResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@HandledBy(handler = RegisterCommandHandler.class)
public record RegisterCommand(String username,
                              String password,
                              String email,
                              String firstName,
                              String lastName,
                              String avatar) implements Command<ResponseEntity<IResponse>> {

}

@Service
@RequiredArgsConstructor
class RegisterCommandHandler implements CommandHandler<RegisterCommand, ResponseEntity<IResponse>> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity<IResponse> handle(RegisterCommand command) {
        User user = new User();

        user.setUsername(command.username());
        user.setPassword(command.password());
        user.setEmail(command.email());
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setAvatar(command.avatar());

        Role role = roleRepository.findByRoleName("ROLE_USER");
        user.setRoles(List.of(role));

        userRepository.save(user);

        return ResponseEntity.status(201).body(new ApiResponse(201, "User registered successfully", null));
    }
}