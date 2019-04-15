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
public class Transfer {

    @ApiModelProperty(dataType = "String", notes = "Required when currency name dublicated on several platfroms")
    @NonNull String platformName;

    @ApiModelProperty(dataType = "String", required = true)
    @NonNull String currencyName;

    @ApiModelProperty(dataType = "String", required = true)
    @NonNull String toAddress;

    @ApiModelProperty(dataType = "Double", required = true)
    @NonNull Double amount;
}
