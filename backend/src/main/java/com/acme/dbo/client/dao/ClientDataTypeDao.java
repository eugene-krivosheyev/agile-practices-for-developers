package com.acme.dbo.client.dao;

import com.acme.dbo.client.domain.ClientDataType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientDataTypeDao extends CrudRepository<ClientDataType, Integer> {
    Optional<ClientDataType> findByDescription(String description);
}
