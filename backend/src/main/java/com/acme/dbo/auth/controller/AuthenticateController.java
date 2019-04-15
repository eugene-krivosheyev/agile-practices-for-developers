package com.acme.dbo.auth.controller;

import com.acme.dbo.auth.domain.AuthToken;
import com.acme.dbo.auth.service.AuthenticationService;
import com.acme.dbo.commons.controller.BackendError;
import com.acme.dbo.config.security.TokenAuthenticationFilter;
import com.acme.dbo.client.dao.ClientDao;
import com.acme.dbo.client.domain.Client;
import com.acme.dbo.client.domain.ClientAuth;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

/*
TODO разделить запросы:
- требует/не требует auht
- пролонгирует/не пролонгирует токен
 */
@RestController
@RequestMapping(value = "/api/authenticate", headers = "X-API-VERSION")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
@Validated
public class AuthenticateController {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired ClientDao clientDao;

    @PostMapping(value = "/login")
    @ApiOperation(value = "login", notes = "Authorization client", response = AuthToken.class)
    public ResponseEntity<Object> authenticate(@AuthenticationPrincipal @RequestBody @Valid final ClientAuth client) {
        if (client == null)
            return new ResponseEntity<>(new BackendError(12009, "Auth data empty"), HttpStatus.OK);

        if (client.getLogin() == null || client.getLogin().isEmpty())
            return new ResponseEntity<>(new BackendError(12009, "Login empty"), HttpStatus.OK);

        if (client.getPassword() == null || client.getPassword().isEmpty())
            return new ResponseEntity<>(new BackendError(12009, "Password empty"), HttpStatus.OK);

        Optional<Client> byLogin = clientDao.findByLogin(client.getLogin());
        if (!byLogin.isPresent())
            return new ResponseEntity<>(new BackendError(12009, "CLient not found"), HttpStatus.OK);

        Optional<AuthToken> optionalAuthToken = authenticationService.authAndGetToken(client.getLogin(), client.getPassword());
        if (!optionalAuthToken.isPresent())
            return new ResponseEntity<>(new BackendError(12009, "Can not authorized"), HttpStatus.OK);

        return new ResponseEntity<>(optionalAuthToken.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/logout", headers = HttpHeaders.AUTHORIZATION)
    @ApiOperation(value = "logout", notes = "Logout client from system")
    public ResponseEntity<Object> deAuthenticate(HttpServletRequest request) {
        //todo add check token code!!! mast have!!!
        String checkToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("Got users token: " + checkToken);
        if(!checkToken.contains(TokenAuthenticationFilter.BEARER))
            return new ResponseEntity<>(new BackendError(12009, "Invalid token"), HttpStatus.OK);
        String token = checkToken.substring(TokenAuthenticationFilter.BEARER.length() + 1);

        authenticationService.deAuth(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
