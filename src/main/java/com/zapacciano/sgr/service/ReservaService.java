package com.zapacciano.sgr.service;

import com.zapacciano.sgr.model.Cliente;
import com.zapacciano.sgr.model.EstadoMesa;
import com.zapacciano.sgr.model.EstadoReserva;
import com.zapacciano.sgr.model.Mesa;
import com.zapacciano.sgr.model.Reserva;
import com.zapacciano.sgr.repository.ClienteRepository;
import com.zapacciano.sgr.repository.MesaRepository;
import com.zapacciano.sgr.repository.ReservaRepository;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MesaRepository mesaRepository;
    
    private static final double MONTO_SEÑA_POR_PERSONA = 10000.00;
   

    // Horarios
    private static final LocalTime HORA_APERTURA = LocalTime.of(12, 0); 
    //Si la última reserva es a las 22:00, el restaurante cierra a las 00:00
    private static final LocalTime HORA_ULTIMA_RESERVA = LocalTime.of(22, 0); 
    

    @Transactional
    public Reserva crearReserva(String nombreCliente, String telefonoCliente, String emailCliente, 
                                LocalDateTime fechaHora, int cantidadPersonas) {
        
        
         // Valida el Horario del Restaurante
        LocalTime horaReserva = fechaHora.toLocalTime();
        if (horaReserva.isBefore(HORA_APERTURA) || horaReserva.isAfter(HORA_ULTIMA_RESERVA)) {
            throw new RuntimeException(String.format(
                "Horario no válido. Nuestras reservas son de %s a %s hs.",
                HORA_APERTURA.toString(), HORA_ULTIMA_RESERVA.toString()
            ));
        }
        
        // Buscar la Mesa Específica 
        Optional<Mesa> mesaOpt = mesaRepository.findFirstByCapacidadGreaterThanEqualAndEstadoOrderByIdAsc(cantidadPersonas,
        EstadoMesa.LIBRE);

             // Validar Capacidad
        if (mesaOpt.isEmpty()) {
            throw new RuntimeException(String.format(
                "No hay mesas LIBRES disponibles con capacidad para %d personas en este momento.", cantidadPersonas));
        }

        Mesa mesaAsignada = mesaOpt.get();

        
        // Busca o crear el cliente
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
            .orElseGet(() -> {
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNombre(nombreCliente);     
                nuevoCliente.setTelefono(telefonoCliente); 
                nuevoCliente.setEmail(emailCliente);       
                return clienteRepository.save(nuevoCliente);
            });

        // Crear y guardar la reserva
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente); 
        reserva.setCantidadPersonas(cantidadPersonas);
        reserva.setMontoSeña(MONTO_SEÑA_POR_PERSONA * cantidadPersonas);
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setMesa(mesaAsignada);
        reserva.setFechaHora(fechaHora); 

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva confirmarPago(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservaId));
        
        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            
            Mesa mesaReservada = reserva.getMesa();
            if (mesaReservada != null && mesaReservada.getEstado() == EstadoMesa.LIBRE) {
                mesaReservada.setEstado(EstadoMesa.RESERVADO);
                mesaRepository.save(mesaReservada);
            }
            return reservaRepository.save(reserva);
        }
        return reserva;
    }

    @Transactional(readOnly = true)
    public Reserva findById(Long id) {
        return reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
    }
}