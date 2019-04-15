package com.acme.dbo;

import com.acme.dbo.commons.Utils;
import com.acme.dbo.config.security.TokenAuthenticationFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.acme.dbo.client.domain.Client;
import com.acme.dbo.auth.domain.AuthToken;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.util.Random;

import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
class Request {
    public static final String API_AUTHENTICATE_URL = "/api/authenticate/login";
    public static final String API_CLIENT_URL = "/api/client";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper jsonMapper;
    @Autowired Random randomizer;

    String clientToString(Client client) throws JsonProcessingException {
        return jsonMapper.writerFor(Client.class).writeValueAsString(client);
    }

    Client clientFromString(String jsonStringWithClient) throws IOException {
        return jsonMapper.readerFor(Client.class).readValue(jsonStringWithClient);
    }

    Client newClient() {
        String login = randomizer.nextInt() + "@ya.ru";
        return Client.builder()
                .login(login)
                .username(login)
                .secret("secret")
                .salt("salt")
                .locale("ru-RU")
                .build();
    }


    MockHttpServletRequestBuilder postRequest(String url) {
        return withApiVersionHeader(post(url))
                .contentType(MediaType.APPLICATION_JSON);
    }

    MockHttpServletRequestBuilder getRequest(String url) {
        return withApiVersionHeader(get(url));
    }


    ResultActions postAuthRequest(String login, String secret) throws Exception {
        return postRequestWithBody(API_AUTHENTICATE_URL,
                        clientToString(
                            Client.builder()
                                .login(login)
                                .username(login)
                                .secret(secret)
                            .build()
                ));
    }


    ResultActions successAuthenticate() throws Exception {
        return postAuthRequest("admin", "749f09bade8aca755660eeb17792da880218d4fbdc4e25fbec279d7fe9f65d70"); //"adminpassword"
    }

    ResultActions absentAuthenticate() throws Exception {
        return postAuthRequest("absent", "d6a7cd2a7371b1a15d543196979ff74fdb027023ebf187d5d329be11055c77fd"); //"any"
    }

    ResultActions disabledAuthenticate() throws Exception {
        return postAuthRequest("disabled", "d6a7cd2a7371b1a15d543196979ff74fdb027023ebf187d5d329be11055c77fd"); //"any"
    }

    ResultActions invalidSecretAuthenticate() throws Exception {
        return postAuthRequest("admin", "539e915a40033497f3a93ce662c8c1940c84503361223312e6fab7c5f3a3fdda"); //"wrong-secret"
    }

    ResultActions postRequestWithBody(String url, String body) throws Exception {
        return mockMvc.perform(postRequest(url)
                .content(body));
    }

    ResultActions authenticatedGetRequest(String url, AuthToken token) throws Exception {
        return mockMvc.perform(withAuthHeader(getRequest(url), token));
    }

    ResultActions authenticatedPostRequest(String url, AuthToken token) throws Exception {
        return mockMvc.perform(withAuthHeader(postRequest(url), token));
    }

    ResultActions authenticatedDeleteRequest(String url, AuthToken token) throws Exception {
        return mockMvc.perform(withAuthHeader(withApiVersionHeader(delete(url)), token));
    }

    MockHttpServletRequestBuilder withAuthHeader(MockHttpServletRequestBuilder request, AuthToken token) throws Exception {
        return request.header(
                HttpHeaders.AUTHORIZATION,
                TokenAuthenticationFilter.BEARER + " " + token.getToken());
    }

    MockHttpServletRequestBuilder withApiVersionHeader(MockHttpServletRequestBuilder request) {
        return request.header("X-API-VERSION", "1");
    }

    ResultActions createClientRequest(String client) throws Exception {
        return postRequestWithBody(API_CLIENT_URL, client);
    }


    Client getClient(AuthToken token) throws Exception {
        return clientFromString(
                getClientRequest(token)
                    .andReturn().getResponse().getContentAsString()
        );
    }

    ResultActions getClientRequest(AuthToken token) throws Exception {
        return authenticatedGetRequest(API_CLIENT_URL + "/info", token);
    }

    Client createClient(Client client) throws Exception {
        client.setPassword(Utils.sha(client.getPassword()));
        return clientFromString(
                createClientRequest(clientToString(client))
                        .andExpect(status().isCreated())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    ResultActions deleteClientRequest(Long id, AuthToken token) throws Exception {
        return authenticatedDeleteRequest(API_CLIENT_URL + "/" + id, token);
    }


    AuthToken successAuthenticateAndGetToken() throws Exception {
        return jsonMapper.readerFor(AuthToken.class).readValue(
                successAuthenticate().andReturn().getResponse().getContentAsString()
        );
    }


    static ResultMatcher hasContentWithSubstring(String substring) {
        return content().string(containsString(substring));
    }

    static ResultMatcher hasHeader(String header, String value) {
        return header().string(header, value);
    }

    static ResultMatcher hasStatus(HttpStatus status) {
        return status().is(status.value());
    }

    AuthToken authenticateWithCredentialsAndGetToken(String login, String password) throws Exception {
        return jsonMapper.readerFor(AuthToken.class).readValue(
                postAuthRequest(login, password).andReturn().getResponse().getContentAsString()
        );
    }

    ResultActions authenticateRequestWithCredentialsAndGetToken(String login, String password) throws Exception {
        return postAuthRequest(login, password);
    }
}
