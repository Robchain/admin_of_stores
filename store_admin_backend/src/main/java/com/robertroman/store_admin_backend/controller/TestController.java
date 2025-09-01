package com.robertroman.store_admin_backend.controller;

import com.robertroman.store_admin_backend.entity.Usuario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        // Solo para forzar que importe la entidad
        Usuario usuario = new Usuario();
        return "Test funcionando - entidad Usuario importada correctamente: " + usuario.getClass().getSimpleName();
    }
}