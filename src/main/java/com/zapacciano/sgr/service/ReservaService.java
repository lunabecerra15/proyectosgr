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
import org.springframework.data.domain.Sort; 
import java.util.List;
import java.time.LocalDate;


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

    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodasLasReservas() {
        // Trae todas las reservas ordenadas: la más nueva arriba
        return reservaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHora"));


    }


    @Transactional
    public void registrarPagoTransferencia(Long reservaId, String codigo) {
        Reserva reserva = findById(reservaId);
        
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("La reserva no está pendiente de pago.");
        }

        reserva.setCodigoComprobante(codigo);
        reserva.setEstado(EstadoReserva.EN_REVISION);
        
        reservaRepository.save(reserva);
    }

    /**
     * El Admin aprueba la transferencia manualmente.
     * La reserva pasa a CONFIRMADA y se bloquea la mesa.
     */
    @Transactional
    public void aprobarReserva(Long reservaId) {
        Reserva reserva = findById(reservaId);
        
        // Solo aprobamos si está en revisión
        if (reserva.getEstado() == EstadoReserva.EN_REVISION) {
            
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            
            // Ocupamos la mesa (igual que con tarjeta)
            Mesa mesa = reserva.getMesa();
            if (mesa != null && mesa.getEstado() == EstadoMesa.LIBRE) {
                mesa.setEstado(EstadoMesa.RESERVADO);
                mesaRepository.save(mesa);
            }
            
            reservaRepository.save(reserva);
        }
    }

    /**
     * Trae las reservas desde HOY a las 00:00 en adelante.
     * Ordenadas por fecha ascendente (lo que va a pasar más pronto, primero).
     */
    /**
     * Trae las reservas desde HOY en adelante.
     * FILTRO: Solo muestra las CONFIRMADA o EN_REVISION.
     * (Oculta las PENDIENTE, CANCELADA, etc.)
     */
    @Transactional(readOnly = true)
    public List<Reserva> obtenerReservasActivas() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        
        // Definimos qué estados queremos ver en la pantalla principal
        List<EstadoReserva> estadosVisibles = List.of(
            EstadoReserva.CONFIRMADA, 
            EstadoReserva.EN_REVISION
        );
        
        return reservaRepository.findByFechaHoraAfterAndEstadoIn(
            inicioHoy, 
            estadosVisibles, 
            Sort.by(Sort.Direction.ASC, "fechaHora")
        );
    }


    /**
     * Trae TODAS las reservas históricas (incluso las viejas).
     */
    @Transactional(readOnly = true)
    public List<Reserva> obtenerHistorialCompleto() {
        // Ordenadas: las más nuevas primero
        return reservaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHora"));
    }

}