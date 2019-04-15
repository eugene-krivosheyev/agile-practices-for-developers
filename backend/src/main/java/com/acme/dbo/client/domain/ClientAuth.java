package com.acme.dbo.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Data
@FieldDefaults(level = PUBLIC)
//@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientAuth {

    @ApiModelProperty(required = true, example = "admin@email.com", notes = "min 5 simbols")
    @Nullable @Pattern(regexp = "^[a-zA-Z0-9_@\\-\\\\.]+$") @Size(min = 5, max = 128)
    String login;

    @ApiModelProperty(required = true, example = "admin4password", notes = "min 5 simbols")
    @Nullable @Pattern(regexp = "^[a-zA-Z0-9_@\\-\\\\.]+$") @Size(min = 5, max = 128)
    String password;
}
