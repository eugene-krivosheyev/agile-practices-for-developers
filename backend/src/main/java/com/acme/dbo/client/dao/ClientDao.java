package com.acme.dbo.client.dao;

import com.acme.dbo.commons.Utils;
import com.acme.dbo.client.domain.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.Random;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isEmpty;

public interface ClientDao extends CrudRepository<Client, Long>, ClientSaveCustomizable<Client> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Client c set c.enabled = TRUE where c.id = :#{#client.id}")
    Client activate(@Param("client") Client client);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Client c set c.enabled = FALSE where c.id = :clientId")
    void delete(@Param("clientId") Long clientId);

    @Query("select c from Client c where c.id = :clientId and c.enabled = TRUE")
    Optional<Client> findById(Long clientId);

    @Query("select c from Client c where c.login = :login and c.enabled = TRUE")
    Optional<Client> findByLogin(String login);
}

interface ClientSaveCustomizable<T>  {
    T save(T entity);
    Optional<Client> findActiveClientByLoginAndSecret(String login, String secret);
}

@Slf4j
class ClientSaveCustomizableImpl implements ClientSaveCustomizable<Client> {
    @Autowired EntityManager entityManager;

    public Optional<Client> findActiveClientByLoginAndSecret(String login, String secret) {
        try {

            TypedQuery<String> saltOfActiveClientByLogin = entityManager.createQuery(
                    "select c.salt from Client c where c.login = :login and c.enabled = TRUE",
                    String.class);
            saltOfActiveClientByLogin.setParameter("login", login);
            String salt = saltOfActiveClientByLogin.getSingleResult();

            log.info("Salt found: " + salt);

            TypedQuery<Client> activeClientByLoginAndSecret = entityManager.createQuery(
                    "select c from Client c where c.login = :login and c.secret = :secret",
                    Client.class);
            activeClientByLoginAndSecret.setParameter("login", login);
            activeClientByLoginAndSecret.setParameter("secret", Utils.sha(format("%s%s", secret, salt)));

            return Optional.of(activeClientByLoginAndSecret.getSingleResult());

        } catch (NoResultException e) {
            log.info("No client found: ", e);
        }
        return Optional.empty();
    }

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    public Client save(Client client) {
        String salt;
        if (isNull(client.getSalt()) || isEmpty(client.getSalt())) {
            salt = generateSalt();
        } else {
            salt = client.getSalt();
        }

        String secret = client.getSecret();
        log.info("salt: " + salt);
        log.info("secret: " + secret);

        client.setSalt(salt);
        client.setPassword(
            Utils.sha(format("%s%s", secret, salt))
        );

        entityManager.persist(client);
        entityManager.flush();
        return client;
    }

    private String generateSalt() {
        String alphabet = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 12; i++)
            res.append(alphabet.charAt(new Random().nextInt(alphabet.length())));
        return res.toString();
    }
}
