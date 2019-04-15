package com.acme.dbo;

import com.acme.dbo.account.dao.AccountRepository;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.*;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class ExampleTest {
    @MockBean private AccountRepository accountRepository;

    @Test
    public void exampleTest() {
        given(accountRepository.findById(anyLong())).willReturn(Optional.empty());
        assertThat("demo string").contains("demo");
    }
}
