package com.acme.dbo.account.service;

import com.acme.dbo.account.domain.Address;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class AccountService {

    public Address createAddress(String platformAlias, String currencyName) {

        return Address.builder()
            .hash(HexUtils.toHexString(UUID.randomUUID().toString().getBytes()))
            .platformId(1L)
            .clientId(4L)
            .createStamp(Instant.now(Clock.systemUTC()))
            .build();
    }

}
