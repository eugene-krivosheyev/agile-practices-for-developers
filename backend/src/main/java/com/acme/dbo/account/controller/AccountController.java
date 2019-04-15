package com.acme.dbo.account.controller;

import com.acme.dbo.account.domain.*;
import com.acme.dbo.config.security.TokenAuthenticationFilter;
import com.acme.dbo.auth.dao.AuthTokenDao;
import com.acme.dbo.client.dao.ClientDao;
import com.acme.dbo.commons.controller.BackendError;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(value = "/api/account", headers = "X-API-VERSION")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
public class AccountController {

    @Autowired ClientDao clientDao;
    @Autowired AuthTokenDao tokens;

    //todo will removed
    private String[] platrofms = new String[]{"btc", "eth", "dash", "xrp", "ont", "btcg"};

    private CurrencyInfo genCurrencyInfo(String name, Long clientId) {
        Integer count = new Random().nextInt(20);

        CurrencyInfo info = new CurrencyInfo();
        info.setName(name);
        info.setAmount(new Random().nextInt(1000) + new Random().nextDouble());
        info.setHashes(new ArrayList<String>());

        for (int i = 0; i < count; i++)
            info.getHashes().add(HexUtils.toHexString(UUID.randomUUID().toString().getBytes()));

        return info;
    }
    private List<PlatformInfo> genPlatformInfo(Long clientId) {
        ArrayList<PlatformInfo> list = new ArrayList<>();

        for (int i = 0; i < platrofms.length; i++)
            list.add(PlatformInfo.builder()
                .name(platrofms[i])
                .amount(new Random().nextInt(1000) + new Random().nextDouble())
                .build());

        return list;
    }

    @PostMapping(headers = HttpHeaders.AUTHORIZATION)
    @ApiOperation(value = "GetAccounts", notes = "Returned all created address of selected currency name")
    public ResponseEntity<Object> getAccounts(HttpServletRequest request) {
        String checkToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!checkToken.contains(TokenAuthenticationFilter.BEARER))
            return new ResponseEntity<>(new BackendError(12009, "Invalid token"), HttpStatus.OK);
        String token = checkToken.substring(TokenAuthenticationFilter.BEARER.length() + 1);

        Long clientId = tokens.findById(token)
            .map(it -> clientDao.findById(it.getClientId()).map(ti -> ti.getId()).orElse(0L))
            .orElse(0L);
        return ResponseEntity.ok(genPlatformInfo(clientId));
    }

    @PostMapping(value = "/info", headers = HttpHeaders.AUTHORIZATION)
    @ApiOperation(value = "Info", notes = "Returned balance by platforms")
    public ResponseEntity<Object> balance(@RequestBody CurrencyOnlyName currencyOnlyName, HttpServletRequest request) {
        String checkToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!checkToken.contains(TokenAuthenticationFilter.BEARER))
            return new ResponseEntity<>(new BackendError(12009, "Invalid token"), HttpStatus.OK);
        String token = checkToken.substring(TokenAuthenticationFilter.BEARER.length() + 1);

        if(currencyOnlyName == null)
            return new ResponseEntity<>(new BackendError(12009, "Empty request data"), HttpStatus.OK);
        if (currencyOnlyName.getName() == null || currencyOnlyName.getName().isEmpty())
            return new ResponseEntity<>(new BackendError(12009, "Currency name empty"), HttpStatus.OK);

        Long clientId = tokens.findById(token)
            .map(it -> clientDao.findById(it.getClientId()).map(ti -> ti.getId()).orElse(0L))
            .orElse(0L);
        return ResponseEntity.ok(genCurrencyInfo(currencyOnlyName.getName(), clientId));
    }

    @PostMapping(value = "/address/create", headers = HttpHeaders.AUTHORIZATION)
    @ApiOperation(value = "CreateAddress", notes = "Create address of platform")
    public ResponseEntity<Object> createAddress(@RequestBody CreatedAddress createdAddress, HttpServletRequest request) {
        String checkToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!checkToken.contains(TokenAuthenticationFilter.BEARER))
            return new ResponseEntity<>(new BackendError(12009, "Invalid token"), HttpStatus.OK);
        String token = checkToken.substring(TokenAuthenticationFilter.BEARER.length() + 1);

        if (createdAddress == null)
            return new ResponseEntity<>(new BackendError(12009, "Created address empty"), HttpStatus.OK);

        if (createdAddress.getCurrencyName() == null || createdAddress.getCurrencyName().isEmpty() || !(createdAddress.getCurrencyName() instanceof String))
            return new ResponseEntity<>(new BackendError(12009, "Wrong currency name of created address empty"), HttpStatus.OK);

        Long clientId = tokens.findById(token)
            .map(it -> clientDao.findById(it.getClientId()).map(ti -> ti.getId()).orElse(0L))
            .orElse(0L);
        return ResponseEntity.ok(Address.builder()
            .clientId(clientId)
            .platformId(new Random().nextLong())
            .createStamp(Instant.now())
            .hash(HexUtils.toHexString(UUID.randomUUID().toString().getBytes()))
            .build()
        );
    }

    @PostMapping(value = "/transfer", headers = HttpHeaders.AUTHORIZATION)
    @ApiOperation(value = "Transfer", notes = "Transfer client coins to selected address")
    public ResponseEntity<Object> transfer(@RequestBody @ApiParam(value = "Transfer", type = "Transfer", required = true) Transfer transfer, HttpServletRequest request) {
        String checkToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!checkToken.contains(TokenAuthenticationFilter.BEARER))
            return new ResponseEntity<>(new BackendError(12009, "Invalid token"), HttpStatus.OK);
        String token = checkToken.substring(TokenAuthenticationFilter.BEARER.length() + 1);

        if (transfer == null)
            return new ResponseEntity<>(new BackendError(12009, "Empty transfer data"), HttpStatus.OK);

        if (transfer.getAmount() <= 0.0)
            return new ResponseEntity<>(new BackendError(12009, "Wrong amount"), HttpStatus.OK);

        if (transfer.getCurrencyName() == null || transfer.getCurrencyName().isEmpty())
            return new ResponseEntity<>(new BackendError(12009, "Empty cyrrency name"), HttpStatus.OK);

        if (transfer.getToAddress() == null || transfer.getToAddress().isEmpty())
            return new ResponseEntity<>(new BackendError(12009, "Empty address"), HttpStatus.OK);

        Long clientId = tokens.findById(token)
            .map(it -> clientDao.findById(it.getClientId()).map(ti -> ti.getId()).orElse(0L))
            .orElse(0L);

        if (clientId == 0L)
            return new ResponseEntity<>(new BackendError(12009, "Client not found"), HttpStatus.OK);

        return ResponseEntity.ok(TransactionStatus.builder()
            .txid(UUID.randomUUID().toString())
            .statusDescription("complete")
            .postStamp(Instant.now().minusSeconds(new Random().nextInt(5)))
            .updateStamp(Instant.now().minusSeconds(new Random().nextInt(3)))
            .build()
        );
    }
}
