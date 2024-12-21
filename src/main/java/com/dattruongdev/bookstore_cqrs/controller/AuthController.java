package com.dattruongdev.bookstore_cqrs.controller;

import com.dattruongdev.bookstore_cqrs.core.auth.command.LoginCommand;
import com.dattruongdev.bookstore_cqrs.core.auth.command.RegisterCommand;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;
import com.dattruongdev.bookstore_cqrs.response.IResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.root}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final DispatchableHandler dispatchableHandler;

    @PostMapping("login")
    public ResponseEntity<IResponse> login(@RequestBody LoginCommand command) {
        return dispatchableHandler.dispatch(command);
    }

    @PostMapping("register")
    public ResponseEntity<IResponse> register(@RequestBody RegisterCommand command) {
        return dispatchableHandler.dispatch(command);
    }
}
