package com.acme.dbo.account.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.PositiveOrZero;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PlatformInfo {

    @ApiModelProperty(dataType = "String", required = true)
    @NonNull String name;

    @ApiModelProperty(dataType = "Double")
    @NonNull @PositiveOrZero Double amount;

}
