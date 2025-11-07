package com.zapacciano.sgr.service;

import com.zapacciano.sgr.model.Cliente;
import com.zapacciano.sgr.model.EstadoReserva;
import com.zapacciano.sgr.model.Reserva;
import com.zapacciano.sgr.repository.ClienteRepository;
import com.zapacciano.sgr.repository.ReservaRepository;
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
    
    private static final double MONTO_SEÑA_POR_PERSONA = 10000.00;

    @Transactional
    public Reserva crearReserva(String nombreCliente, String telefonoCliente, String emailCliente, 
                                LocalDateTime fechaHora, int cantidadPersonas) {
        
        // --- ¡ESTA LÍNEA (33) AHORA FUNCIONA! ---
        Cliente cliente = clienteRepository.findByEmail(emailCliente)
            .orElseGet(() -> {
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNombre(nombreCliente);
                nuevoCliente.setTelefono(telefonoCliente);
                nuevoCliente.setEmail(emailCliente);
                return clienteRepository.save(nuevoCliente);
            });

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setFechaHora(fechaHora);
        reserva.setCantidadPersonas(cantidadPersonas);
        
        double montoTotal = MONTO_SEÑA_POR_PERSONA * cantidadPersonas;
        reserva.setMontoSeña(montoTotal);
        
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setMesa(null); 

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva confirmarPago(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + reservaId));
        
        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
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