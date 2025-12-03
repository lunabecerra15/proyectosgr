package com.zapacciano.sgr.model;

/**
 * Define el ciclo de vida de una Reserva y su seña.
 */
public enum EstadoReserva {
    PENDIENTE,
    EN_REVISION,   // El cliente reservó, pero aún no pagó la seña.
    CONFIRMADA,  // ¡Ya pagó! Su mesa está guardada.
    APLICADA,    // El cliente asistió y la seña ya se descontó de un Pedido.
    CANCELADA,   // El cliente canceló.
    NO_SHOW      // El cliente nunca apareció.
}
