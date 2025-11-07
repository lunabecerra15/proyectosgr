package com.zapacciano.sgr.controller;

import com.zapacciano.sgr.model.Mesa;
import com.zapacciano.sgr.repository.MesaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Este controlador es SOLO para datos (JSON), no para vistas HTML.
 * Es la API que consumirá nuestro JavaScript.
 */
@RestController
@RequestMapping("/api/mesas") // Todas las URLs aquí empiezan con /api/mesas
public class MesaApiController {

    private final MesaRepository mesaRepository;

    public MesaApiController(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    /**
     * Devuelve una lista de TODAS las mesas y su estado.
     * El cliente (JS) llamará a esta URL.
     */
    @GetMapping
    public List<Mesa> getEstadoDeTodasLasMesas() {
        // Spring Boot convertirá esta lista de Java a JSON automáticamente
        return mesaRepository.findAll();
    }
}