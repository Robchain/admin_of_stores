package com.robertroman.store_admin_backend.service;

import com.robertroman.store_admin_backend.entity.Local;
import com.robertroman.store_admin_backend.entity.Usuario;
import com.robertroman.store_admin_backend.repository.LocalRepository;
import com.robertroman.store_admin_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocalService {

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear local
    public Local crearLocal(Local local, Long usuarioId) {
        // Verificar que existe el usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
        }

        local.setUsuario(usuarioOpt.get());
        return localRepository.save(local);
    }

    // Actualizar local
    public Local actualizarLocal(Long id, Local localActualizado) {
        Optional<Local> localExistente = localRepository.findById(id);

        if (localExistente.isEmpty()) {
            throw new RuntimeException("Local no encontrado con ID: " + id);
        }

        Local local = localExistente.get();

        // Actualizar campos (sin cambiar el usuario propietario)
        local.setNombre(localActualizado.getNombre());
        local.setDireccion(localActualizado.getDireccion());
        local.setTelefono(localActualizado.getTelefono());
        local.setCiudad(localActualizado.getCiudad());
        local.setActivo(localActualizado.getActivo());

        return localRepository.save(local);
    }

    // Obtener local por ID
    public Optional<Local> obtenerPorId(Long id) {
        return localRepository.findById(id);
    }

    // Obtener locales por usuario
    public List<Local> obtenerLocalesPorUsuario(Long usuarioId) {
        return localRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    // Obtener todos los locales activos
    public List<Local> obtenerLocalesActivos() {
        return localRepository.findByActivoTrue();
    }

    // Buscar locales por nombre
    public List<Local> buscarPorNombre(String nombre) {
        return localRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    // Obtener locales por ciudad
    public List<Local> obtenerPorCiudad(String ciudad) {
        return localRepository.findByCiudadAndActivoTrue(ciudad);
    }

    // Obtener todas las ciudades
    public List<String> obtenerCiudades() {
        return localRepository.findAllCiudades();
    }

    // Contar locales por usuario
    public Long contarLocalesPorUsuario(Long usuarioId) {
        return localRepository.countByUsuarioId(usuarioId);
    }

    // Obtener locales con ventas en período
    public List<Local> obtenerLocalesConVentasEnPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return localRepository.findLocalesConVentasEnPeriodo(fechaInicio, fechaFin);
    }

    // Verificar si el usuario es propietario del local
    public boolean esUsuarioPropietario(Long localId, Long usuarioId) {
        Optional<Local> localOpt = localRepository.findById(localId);
        if (localOpt.isPresent()) {
            return localOpt.get().getUsuario().getId().equals(usuarioId);
        }
        return false;
    }

    // Desactivar local (soft delete)
    public void desactivarLocal(Long id, Long usuarioId) {
        Optional<Local> localOpt = localRepository.findById(id);
        if (localOpt.isEmpty()) {
            throw new RuntimeException("Local no encontrado con ID: " + id);
        }

        Local local = localOpt.get();

        // Verificar que el usuario es el propietario
        if (!local.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para desactivar este local");
        }

        local.setActivo(false);
        localRepository.save(local);
    }

    // Activar local
    public void activarLocal(Long id, Long usuarioId) {
        Optional<Local> localOpt = localRepository.findById(id);
        if (localOpt.isEmpty()) {
            throw new RuntimeException("Local no encontrado con ID: " + id);
        }

        Local local = localOpt.get();

        // Verificar que el usuario es el propietario
        if (!local.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para activar este local");
        }

        local.setActivo(true);
        localRepository.save(local);
    }

    // Validar acceso del usuario al local
    public void validarAccesoLocal(Long localId, Long usuarioId) {
        if (!esUsuarioPropietario(localId, usuarioId)) {
            throw new RuntimeException("No tienes acceso a este local");
        }
    }

    // Obtener estadísticas básicas del local
    public LocalEstadisticas obtenerEstadisticasLocal(Long localId) {
        Optional<Local> localOpt = localRepository.findById(localId);
        if (localOpt.isEmpty()) {
            throw new RuntimeException("Local no encontrado con ID: " + localId);
        }

        Local local = localOpt.get();
        LocalEstadisticas estadisticas = new LocalEstadisticas();
        estadisticas.setLocal(local);
        estadisticas.setCantidadProductos(local.getProductoLocales().size());
        estadisticas.setCantidadVentas(local.getVentas().size());

        return estadisticas;
    }

    // Clase interna para estadísticas
    public static class LocalEstadisticas {
        private Local local;
        private Integer cantidadProductos;
        private Integer cantidadVentas;

        // Getters y Setters
        public Local getLocal() { return local; }
        public void setLocal(Local local) { this.local = local; }

        public Integer getCantidadProductos() { return cantidadProductos; }
        public void setCantidadProductos(Integer cantidadProductos) { this.cantidadProductos = cantidadProductos; }

        public Integer getCantidadVentas() { return cantidadVentas; }
        public void setCantidadVentas(Integer cantidadVentas) { this.cantidadVentas = cantidadVentas; }
    }
}