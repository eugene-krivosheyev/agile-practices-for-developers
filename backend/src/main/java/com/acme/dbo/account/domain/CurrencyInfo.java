package com.acme.dbo.account.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CurrencyInfo {

    @ApiModelProperty(dataType = "String", required = true)
    @NonNull String name;

    @ApiModelProperty(dataType = "Double")
    @NonNull @PositiveOrZero Double amount;

    @ApiModelProperty(dataType = "Array<String>")
//    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NonNull ArrayList<String> hashes;

}
