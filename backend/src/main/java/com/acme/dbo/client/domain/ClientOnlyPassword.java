package com.acme.dbo.client.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientOnlyPassword {

    @EqualsAndHashCode.Exclude
    @Nullable
    @Size(min = 5, max = 128)
    @ApiModelProperty(notes = "New password")
    String password;

}
