package com.acme.dbo.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Past;
import java.sql.Timestamp;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ADDRESS")
public class Address {

    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Long")
    @Nullable Long id;

    @Column(name = "CLIENT_ID", columnDefinition = "INTEGER")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Long")
    @Nullable Long clientId;

    @Column(name = "PLATFORM_ID", columnDefinition = "INTEGER")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Long")
    @Nullable Long platformId;

    @Column(name = "HASH")
    @ApiModelProperty(dataType = "String")
    @NonNull String hash;

    @Column(name = "CREATE_STAMP")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Instant")
    @Nullable @Past Instant createStamp;

}
