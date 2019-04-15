package com.acme.dbo;

import com.acme.dbo.auth.domain.AuthToken;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.NestedServletException;

import javax.validation.ConstraintViolationException;

import static com.acme.dbo.Request.*;
import static lombok.AccessLevel.PRIVATE;
import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
public class AuthenticationIT {
    @Autowired Request request;

    @Test
    public void shouldGetAuthTokenWhenExistingUserAndCorrectCredentials() throws Exception {
        request.successAuthenticate()
            .andExpect(hasStatus(HttpStatus.OK))
            .andExpect(hasHeader(CONTENT_TYPE, "application/json;charset=UTF-8"))
            .andExpect(hasContentWithSubstring("-"));
    }

    @Test
    public void shouldNotGetAuthTokenWhenAbsentUser() throws Exception {
        try {
            request.absentAuthenticate();
        } catch (NestedServletException wrapper) {
            assertThat(wrapper.getCause())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Account not found");
        }
    }

    @Test
    public void shouldNotGetAuthTokenWhenDisabledUser() throws Exception {
        try {
            request.disabledAuthenticate();
        } catch (NestedServletException wrapper) {
            assertThat(wrapper.getCause())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Account not found");
        }
    }

    @Test
    public void shouldNotGetAuthTokenWhenExistingUserAndWrongSecret() throws Exception {
        try {
            request.invalidSecretAuthenticate();
        } catch (NestedServletException wrapper) {
            assertThat(wrapper.getCause())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Account not found");
        }
    }

    @Test
    public void shouldNotGetAuthTokenWhenRequestBodyIsEmpty() throws Exception {
        request.postRequestWithBody(API_AUTHENTICATE_URL, "")
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAuthenticateWhenExistingUserAndCorrectCredentials() throws Exception {
        AuthToken authToken = request.successAuthenticateAndGetToken();
    }

    @Test @Ignore
    public void shouldNotAuthenticateWhenDeletedAuthToken() throws Exception {
        AuthToken authToken = request.successAuthenticateAndGetToken();
        request.authenticatedPostRequest(API_AUTHENTICATE_URL + "/logout", authToken)
            .andExpect(status().isOk());

        request.authenticatedPostRequest(API_CLIENT_URL + "/1", authToken)
            .andExpect(status().isUnauthorized());
    }

    @Test @Ignore
    public void shouldNotAuthenticateWhenParamIsNotInAuthTokenFormat() throws Exception {
        try {
            AuthToken authToken = request.successAuthenticateAndGetToken();
            request.authenticatedPostRequest(API_AUTHENTICATE_URL + "logout/0", authToken)
                    .andExpect(status().isOk());
        } catch (NestedServletException e) {
            assertThat(e.getCause()).isInstanceOf(ConstraintViolationException.class);
            assertThat(e.getCause().getMessage())
                    .contains("must match")
                    .contains("size must be");
        }
    }
}
