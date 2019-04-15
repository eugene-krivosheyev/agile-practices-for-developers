package com.acme.dbo.client.controller;

import com.acme.dbo.auth.service.AuthenticationService;
import com.acme.dbo.client.dao.ClientDao;
import com.acme.dbo.client.dao.ClientDataDao;
import com.acme.dbo.client.domain.Client;
import com.acme.dbo.client.domain.ClientAuth;
import com.acme.dbo.client.domain.ClientData;
import com.acme.dbo.client.domain.ClientResetPassword;
import com.acme.dbo.client.service.ClientValidationService;
import com.acme.dbo.commons.controller.BackendError;
import com.acme.dbo.config.security.TokenAuthenticationFilter;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(value = "/api/client", headers = "X-API-VERSION")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
public class ClientController {
    @Autowired
    ClientDao clients;
    @Autowired
    ClientValidationService clientValidatorService;
    @Autowired
    AuthenticationService tokens;
    @Autowired
    ClientDataDao clientDataDao;
    @Autowired ClientDao clientDao;

    @PostMapping
    @ApiOperation(value = "Registration", notes = "Registered new user in service", response = Client.class)
    @Transactional
    public ResponseEntity<Object> createClient(@RequestBody @Valid final ClientAuth client) {

        Optional<Client> clientByLogin = clients.findByLogin(client.getLogin());
        if (clientByLogin.isPresent()) //TODO OMFG WTF
            if (!clientByLogin.get().isEnabled()) return new ResponseEntity<>(clientByLogin.get(), HttpStatus.CREATED);
            else return new ResponseEntity<>(new BackendError(12001, "User exists"), HttpStatus.OK); //TODO OMFG

        if (client.getLogin() == null || client.getLogin().isEmpty())
            return new ResponseEntity<>(new BackendError(12002, "Invalid login"), HttpStatus.OK);

        if (client.getPassword() == null || client.getPassword().isEmpty())
            return new ResponseEntity<>(new BackendError(12003, "Invalid password"), HttpStatus.OK);

        Client createdClient = clients.save(
            Client.builder()
                .login(client.getLogin())
                .secret(client.getPassword())
                .enabled(false)
                .build());
        if (createdClient == null) return new ResponseEntity<>(new BackendError(12004, "Not registered"), HttpStatus.OK);

        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    @GetMapping(value = "/info", headers = HttpHeaders.AUTHORIZATION)
    @ApiOperation(value = "Info", notes = "Get client information", response = Client.class)
    public ResponseEntity<Object> getClient(HttpServletRequest request) throws Exception {
        String checkToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!checkToken.contains(TokenAuthenticationFilter.BEARER))
            return new ResponseEntity<>(new BackendError(12009, "Invalid token"), HttpStatus.OK);
        String token = checkToken.substring(TokenAuthenticationFilter.BEARER.length() + 1);

        log.info("Got users token: " + token);

        Optional<Client> clientByToken = tokens.findUserByToken(token);
        log.info("Got client by token: " + clientByToken.get());

        if(!clientByToken.isPresent())
            return new ResponseEntity<>(new BackendError(12003, "Client not found"), HttpStatus.OK);

        return new ResponseEntity<>(clientByToken.get(), HttpStatus.OK);
    }

    @PostMapping(value = "/password/update")
    @ApiOperation(value = "UpdatePassword", notes = "Reset client password")
    public ResponseEntity<Object> updatePassword(@RequestBody ClientResetPassword clientResetPassword) {
        if (clientResetPassword == null)
            return new ResponseEntity<>(new BackendError(12003, "Empty request"), HttpStatus.OK);
        if (clientResetPassword.getCode() == null || clientResetPassword.getCode().isEmpty())
            return new ResponseEntity<>(new BackendError(12003, "Empty code"), HttpStatus.OK);
        if (clientResetPassword.getPassword() == null || clientResetPassword.getPassword().isEmpty())
            return new ResponseEntity<>(new BackendError(12003, "Empty password"), HttpStatus.OK);

        ClientData clientData = clientDataDao.findByValue(clientResetPassword.getCode());
        if (clientData == null || clientData.getValue().isEmpty())
            return new ResponseEntity<>(new BackendError(12003, "Can not update password"), HttpStatus.OK);

        Optional<Client> optionalClient = clientDao.findById(clientData.getClientId());
        optionalClient.ifPresent(it -> it.setPassword(clientResetPassword.getPassword()));
        clientDao.save(optionalClient.get());
        clientDataDao.delete(clientData);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
