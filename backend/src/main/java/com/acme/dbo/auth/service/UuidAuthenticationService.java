package com.acme.dbo.auth.service;

import com.acme.dbo.auth.dao.AuthTokenDao;
import com.acme.dbo.auth.domain.AuthToken;
import com.acme.dbo.client.dao.ClientDao;
import com.acme.dbo.client.domain.Client;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UuidAuthenticationService implements AuthenticationService {
    @NonNull ClientDao users;
    @NonNull AuthTokenDao tokens;

    @Override
    public Optional<AuthToken> authAndGetToken(final String login, final String secret) {
         Optional<AuthToken> maybeToken = users.findActiveClientByLoginAndSecret(login, secret)
                .map(client -> AuthToken.builder()
                                .token(randomUUID().toString())
                                .created(Instant.now())
                                .enabled(true)
                                .clientId(client.getId())
                            .build()
                );

         maybeToken.ifPresent(tokens::save);
         return maybeToken;
    }

    @Override
    public Optional<Client> findUserByToken(final String token) {
        return tokens.findById(token)
                .map(AuthToken::getClientId)
                .flatMap(users::findById);
    }

    @Override
    public void deAuth(String token) {
        tokens.deleteById(token);
    }
}
