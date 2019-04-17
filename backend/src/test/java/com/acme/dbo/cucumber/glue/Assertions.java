package com.acme.dbo.cucumber.glue;

import com.acme.dbo.client.domain.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    public static void clientHasEmail(Client clientFound, String email) {
        assertThat(clientFound.getLogin())
                .isEqualTo(email);
    }
}
