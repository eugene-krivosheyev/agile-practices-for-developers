package com.acme.dbo.auth.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Token entity", description = "It entity keep client auth token")
@Entity
@Table(name = "AUTH_TOKEN")
public class AuthToken {
    @NonNull
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    @Size(min = 36, max = 36)
    @ApiModelProperty(name = "token", notes = "UUID", dataType = "String")
    @Column(name = "TOKEN", nullable = false)
    @Id
    String token;

    @ApiModelProperty(name = "created", notes = "Time created token", dataType = "String")
    @Nullable @Past
    @Column(name = "CREATED", insertable = false) //TODO decide where to geberate NOW: app or db
    Instant created;

    @ApiModelProperty(name = "expired", notes = "Time expired token", dataType = "String")
    @Nullable @Past
    @Column(name = "EXPIRED", insertable = false)
    Instant expired;

    @ApiModelProperty(name = "lastAccessStamp", notes = "Last access time stamp by token", dataType = "String")
    @Nullable @Past
    @Column(name = "LAST_ACCESS_STAMP", insertable = false)
    Instant lastAccessStamp;

    @ApiModelProperty(name = "clientId", notes = "Unique identification of client", dataType = "Integer")
    @Nullable @PositiveOrZero
    @Column(name = "CLIENT_ID")
    Long clientId;

    @Column(name = "ENABLED", insertable = false)
    Boolean enabled;
}
