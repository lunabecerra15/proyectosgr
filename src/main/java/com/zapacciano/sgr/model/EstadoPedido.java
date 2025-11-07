package com.zapacciano.sgr.model;

/**
 * Define los estados posibles de un Pedido en el sistema.
 */
public enum EstadoPedido {
    PENDIENTE,  // El Mozo lo acaba de crear
    COMPLETADO, // El Admin lo cerró y cobró
    CANCELADO   // Se canceló
}