package com.acme.dbo.commons.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor
public class BackendError {
    @NonNull @PositiveOrZero Integer code;
    @NonNull @NotEmpty String message;
}
