package com.acme.dbo.auth.dao;

import com.acme.dbo.auth.domain.AuthToken;
import org.springframework.data.repository.CrudRepository;

public interface AuthTokenDao extends CrudRepository<AuthToken, String> { }
