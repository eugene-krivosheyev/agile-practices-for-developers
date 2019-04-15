package com.acme.dbo.client.dao;

import com.acme.dbo.client.domain.ClientStatus;
import org.springframework.data.repository.CrudRepository;

public interface ClientStatusDao extends CrudRepository<ClientStatus, Integer> { }
