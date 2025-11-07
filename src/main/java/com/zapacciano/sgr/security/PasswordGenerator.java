package com.zapacciano.sgr.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
     /**
     * ¡EJECUTA ESTE MÉTODO main() UNA SOLA VEZ!
     * (Puedes hacerlo con clic derecho > Run 'PasswordGenerator.main()')
     */
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // ----> 1. ESCRIBE TU CONTRASEÑA ACTUAL AQUÍ <----
        String tuClaveEnTextoPlano = "1234"; 
        
        // 2. Esto generará el hash
        String tuClaveHasheada = encoder.encode(tuClaveEnTextoPlano);
        
        System.out.println("=============================================================");
        System.out.println("Tu clave en texto plano: " + tuClaveEnTextoPlano);
        System.out.println("¡COPIA ESTE HASH!");
        System.out.println("Pégalo en la columna 'password' de tu usuario en la DB:");
        System.out.println("");
        System.out.println(tuClaveHasheada);
        System.out.println("");
        System.out.println("=============================================================");
    }
    
}
