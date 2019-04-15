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
@Table(name = "CLIENT_DATA")
public class ClientData {
    @Nullable @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @Nullable String value;
    @NonNull Instant postStamp;
    @Nullable Instant expireStamp;
    @NonNull Long clientId;
    @NonNull Integer typeId;
}
