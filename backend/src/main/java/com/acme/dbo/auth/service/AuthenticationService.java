package com.acme.dbo.auth.service;

import com.acme.dbo.auth.domain.AuthToken;
import com.acme.dbo.client.domain.Client;

import java.util.Optional;

public interface AuthenticationService {
    Optional<AuthToken> authAndGetToken(String login, String secret); //TODO hashedPwd
    Optional<Client> findUserByToken(String token);
    void deAuth(String token);
}
