package com.acme.dbo;

import com.acme.dbo.commons.Utils;
import com.acme.dbo.client.domain.Client;
import com.acme.dbo.auth.domain.AuthToken;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.NestedServletException;

import static lombok.AccessLevel.PRIVATE;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class RegistrationIT {
    @Autowired Request request;

    @Test
    public void shouldNoNeedAuthenticationWhenRegisterClient() throws Exception {
        request.createClientRequest(
                request.clientToString(request.newClient())
        ).andExpect(
                status().isCreated());
    }

    @Test
    public void shouldReturnClientWhenRegisterIt() throws Exception {
        Client sourceClient = request.newClient();
        Client createdClient = request.createClient(sourceClient);

        sourceClient.setId(createdClient.getId());
        assertEquals(sourceClient, createdClient);
    }

    @Test
    public void shouldClientExistAfterRegister() throws Exception {
        String password = "password";
        Client newClient = request.newClient();
        newClient.setPassword(password);
        Client createdClient = request.createClient(newClient);

        AuthToken authToken = request.authenticateWithCredentialsAndGetToken(
                createdClient.getLogin(), Utils.sha(password)
        );
        Client gotClient = request.getClient(authToken);

        assertEquals(createdClient, gotClient);
    }

    @Test
    public void shouldNotAuthenticateWhenAccountDeleted() throws Exception {
        Client accountToDelete = request.createClient(
            request.newClient()
        );

        request.deleteClientRequest(
                accountToDelete.getId(),
                request.successAuthenticateAndGetToken()
        ).andExpect(status().isOk());

        try {
            request.postAuthRequest(
                    accountToDelete.getLogin(),
                    accountToDelete.getSecret()
            );
        } catch (NestedServletException wrapper) {
            assertThat(wrapper.getCause())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Client not found");
        }
    }

    @Test
    public void shouldNotGetClientWhenClientDeleted() throws Exception {
        Client clientToDelete = request.createClient(
                request.newClient()
        );

        request.deleteClientRequest(
                clientToDelete.getId(),
                request.successAuthenticateAndGetToken()
        ).andExpect(status().isOk());

        request.authenticateRequestWithCredentialsAndGetToken(
                clientToDelete.getLogin(),
                clientToDelete.getPassword()
        ).andExpect(status().isNotFound());
    }
}
