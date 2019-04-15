package com.acme.dbo.account.domain;

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
@Table(name = "PLATFORM")
public class Platform {

    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(dataType = "Long")
    @Nullable Long id;

    @Column(name = "NAME")
    @ApiModelProperty(dataType = "String")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable String name;

    @Column(name = "ALIAS")
    @ApiModelProperty(dataType = "String")
    @NonNull String alias;
}
