
package com.cartera.backend.config;

import com.cartera.backend.service.RolService;
import com.cartera.backend.service.UsuarioService;
import com.cartera.backend.repository.RolRepository;
import com.cartera.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@ConditionalOnProperty(
    name = "app.seed.enabled",
    havingValue = "true"
)
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolService rolService;
    private final UsuarioService usuarioService;

    @Value("${app.seed.admin.nombre}")
    private String nombre;

    @Value("${app.seed.admin.correo}")
    private String correo;

    @Value("${app.seed.admin.contrasena}")
    private String contrasena;

    public DataInitializer(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            RolService rolService,
            UsuarioService usuarioService) {

        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolService = rolService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {

        if (correo.isBlank() || contrasena.isBlank()) {
            throw new IllegalStateException(
                "Debe configurar ADMIN_EMAIL y ADMIN_PASSWORD"
            );
        }

        String correoNormalizado =
            correo.trim().toLowerCase(Locale.ROOT);

        List<String> roles = List.of(
            "ADMINISTRADOR",
            "ANALISTA",
            "REPRESENTANTE"
        );

        for (String nombreRol : roles) {

            if (!rolRepository.existsByNombre(nombreRol)) {
                rolService.crearRol(nombreRol);
            }
        }

        if (!usuarioRepository.existsByCorreo(correoNormalizado)) {

            var rolAdmin =
                rolService.buscarPorNombre("ADMINISTRADOR");

            usuarioService.crearUsuario(
                nombre,
                correoNormalizado,
                contrasena,
                rolAdmin.getId()
            );

            System.out.println(
                "Administrador inicial creado correctamente."
            );
        }
    }
}

