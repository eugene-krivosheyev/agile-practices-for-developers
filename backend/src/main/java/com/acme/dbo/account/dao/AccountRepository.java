package com.acme.dbo.account.dao;

import com.acme.dbo.account.domain.Account;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
