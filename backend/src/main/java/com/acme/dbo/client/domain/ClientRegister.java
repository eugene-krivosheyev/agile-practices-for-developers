package com.acme.dbo.client.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientRegister {

    @ApiModelProperty(required = true, example = "admin@email.com", notes = "min 5 simbols")
    @NonNull @Pattern(regexp = "^[a-zA-Z0-9_@\\-\\\\.]+$") @Size(min = 5, max = 128)
    String login;

    @ApiModelProperty(required = true, example = "admin4password", notes = "min 5 simbols")
    @NonNull @Pattern(regexp = "^[a-zA-Z0-9_@\\-\\\\.]+$") @Size(min = 5, max = 128)
    String password;

    @ApiModelProperty(notes = "Country of client location")
    @EqualsAndHashCode.Exclude @Nullable
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "LOCALE", insertable = false)
    String locale;

    @ApiModelProperty(notes = "Client name")
    @Nullable @Pattern(regexp = "^[a-zA-Z0-9_@\\-\\\\.]+$") @Size(min = 5, max = 128)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "USERNAME")
    String username;

}
