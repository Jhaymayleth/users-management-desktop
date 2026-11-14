package com.jcaa.usersmanagement.application.service.dto.command;

import jakarta.validation.constraints.NotBlank;

/** Comando para retirar un producto del catálogo del lavadero. */
public record DeleteProductCommand(@NotBlank(message = "id must not be blank") String id) {}
