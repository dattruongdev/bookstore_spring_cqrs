package com.dattruongdev.bookstore_cqrs.core.auth.command;

import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.Command;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command.CommandHandler;
import com.dattruongdev.bookstore_cqrs.response.ErrorResponse;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import com.dattruongdev.bookstore_cqrs.response.JwtResponse;
import com.dattruongdev.bookstore_cqrs.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@HandledBy(handler = LoginCommandHandler.class)
public record LoginCommand(String username, String password) implements Command<ResponseEntity<IResponse>> {
}

@RequiredArgsConstructor
@Service
class LoginCommandHandler implements CommandHandler<LoginCommand, ResponseEntity<IResponse>> {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;


    @Override
    public ResponseEntity<IResponse> handle(LoginCommand command) {
        Authentication auth = new UsernamePasswordAuthenticationToken(command.username(), command.password());
        try {
            auth = authenticationManager.authenticate(auth);
            String token = jwtUtils.generateJwtToken(auth);

            return ResponseEntity.ok().body(new JwtResponse(token));
        } catch(AuthenticationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "Invalid username or password"));
        }
    }
}