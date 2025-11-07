//package com.zapacciano.sgr.repository;

//import com.zapacciano.sgr.model.Usuario;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.util.Optional;

//public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  //  Optional<Usuario> findByEmail(String email);
//}

package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.Usuario; // O como se llame tu entidad
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // ¡Importante!

// (Tu interfaz probablemente se vea así)
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ----> ¡AGREGA ESTA LÍNEA AQUÍ DENTRO! <----
    //
    // Esto le da a Spring la habilidad de buscar usuarios por su email,
    // lo cual es necesario para el CustomUserDetailsService.
    //
    Optional<Usuario> findByEmail(String email);

}
