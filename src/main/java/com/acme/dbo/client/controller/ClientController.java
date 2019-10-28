package com.acme.dbo.client.controller;

import com.acme.dbo.client.dao.ClientRepository;
import com.acme.dbo.client.domain.Client;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@ConditionalOnProperty(name = "features.client", havingValue = "true", matchIfMissing = true)
@RestController
@RequestMapping(value = "/api/client", headers = "X-API-VERSION=1")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
public class ClientController {
    @Autowired ClientRepository clients;

    @ApiOperation(value = "Registration", notes = "Registered new user in service", response = Client.class)
    @ApiResponse(code = 201, message = "Client created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client createClient(@RequestBody @Valid final Client clientDto) {
        return clients.saveAndFlush(clientDto);
    }

    @ApiOperation(value = "Info", notes = "Get all clients", response = Collection.class)
    @GetMapping
    public Collection<Client> getClients() {
        return clients.findAll();
    }

    @ApiOperation(value = "Info", notes = "Get client information", response = Client.class)
    @GetMapping("/{id}")
    public Client getClient(@PathVariable("id") @PositiveOrZero long id) {
        return clients.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("client #" + id));
    }
}
