package com.acme.dbo.account.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.index.qual.Positive;

import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AddressInfo {

    @ApiModelProperty(dataType = "String")
    @NonNull String hash;

    @ApiModelProperty(dataType = "Double")
    @NonNull @PositiveOrZero Double amount;

    @ApiModelProperty(dataType = "Instant")
    @NonNull @Past Instant createStamp;

}
