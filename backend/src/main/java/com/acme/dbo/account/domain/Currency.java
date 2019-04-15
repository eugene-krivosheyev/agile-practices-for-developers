package com.acme.dbo.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.*;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CURRENCY")
public class Currency {

    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Long")
    @Nullable Long id;

    @Column(name = "PLATFORM_ID", columnDefinition = "INTEGER")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Long")
    @Nullable Long platformId;

    @Column(name = "NAME")
    @ApiModelProperty(dataType = "String")
    @NonNull String name;

    @Column(name = "ALIAS")
    @ApiModelProperty(dataType = "String")
    @NonNull String alias;

    @Column(name = "TYPE_ID", columnDefinition = "INTEGER")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "String")
    @Nullable Long typeId;

    @Column(name = "DECIMAL_PRECISION")
    @ApiModelProperty(dataType = "Integer")
    @Nullable Integer decimalPrecision;

}
