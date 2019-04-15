package com.acme.dbo.client.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CLIENT_STATUS")
public class ClientStatus {
    @Nullable @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
    @Nullable @Column(nullable = false) String description;
}
