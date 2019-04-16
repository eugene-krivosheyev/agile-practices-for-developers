package com.acme.dbo.client.dao;

import com.acme.dbo.client.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByLogin(String login);
}