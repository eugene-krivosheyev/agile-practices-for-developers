package com.acme.dbo.account.controller;

import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(value = "/api/account", headers = "X-API-VERSION")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
public class AccountController {
    @Autowired AccountRepository accountRepository;

    @PostMapping
    @ApiOperation(value = "GetAccounts", notes = "Returned all created address of selected currency name")
    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }
}
