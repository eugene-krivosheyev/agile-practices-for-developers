package com.acme.dbo.account.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CreatedAddress {

    @ApiModelProperty(dataType = "String")
    @NonNull String platformName;

    @ApiModelProperty(dataType = "String", required = true)
    @NonNull String currencyName;
}
