package com.acme.dbo.client.dao;

import com.acme.dbo.client.domain.ClientData;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientDataDao extends CrudRepository<ClientData, Integer> {
    Optional<ClientData> findByClientId(Long clientId);
    ClientData findByTypeIdAndClientId(Integer typeId, Long clientId);
    ClientData findByValue(String value);
}
