package com.acme.dbo.client.domain;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientResetPassword {

    @Nullable
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    @Size(min = 36, max = 36)
    @ApiModelProperty(required = true)
    String code;

    @EqualsAndHashCode.Exclude
    @Nullable
    @Size(min = 5, max = 128)
    @ApiModelProperty(required = true, notes = "min 5 max 128 symbols")
    String password;

}
