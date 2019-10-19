package com.acme.dbo.ut;

import com.acme.dbo.account.controller.AccountController;
import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import com.acme.dbo.client.dao.ClientRepository;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisabledIf(expression = "#{environment['features.account'] == 'false'}", loadContext = true)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class AccountControllerTest {
    @Autowired AccountController sut;
    @MockBean AccountRepository accountRepositoryMock;
    @MockBean ClientRepository clientRepositoryDummy;
    @Mock Account accountStub;

    @Test
    public void exampleTest() {
        given(accountRepositoryMock.findAll()).willReturn(singletonList(accountStub));

        assertThat(sut.getAccounts()).containsOnly(accountStub);
        verify(accountRepositoryMock, times(1)).findAll();
    }
}
