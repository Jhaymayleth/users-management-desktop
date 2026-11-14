package com.jcaa.usersmanagement.application.service.dto.query;

import jakarta.validation.constraints.NotBlank;

/** Consulta para recuperar un producto del catálogo por su identificador. */
public record GetProductByIdQuery(@NotBlank(message = "id must not be blank") String id) {}
