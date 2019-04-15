package com.acme.dbo.client.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CLIENT_DATA_TYPE")
public class ClientDataType {
    @NonNull @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
    @NonNull String description;
}
