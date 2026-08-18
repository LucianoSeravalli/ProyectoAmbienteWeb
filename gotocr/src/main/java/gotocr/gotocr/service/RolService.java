package gotocr.gotocr.service;


import gotocr.gotocr.domain.Rol;
import gotocr.gotocr.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> listarRoles() {
        return rolRepository.listarRoles();
    }

    public Optional<Rol> buscarPorId(Integer idRol) {

        validarId(idRol);

        return rolRepository.buscarPorId(idRol);
    }

    public Optional<Rol> buscarPorNombre(String nombreRol) {

        validarTexto(nombreRol, "El nombre del rol es obligatorio");

        return rolRepository.buscarPorNombre(nombreRol.trim());
    }

    public void insertarRol(String nombreRol) {

        validarTexto(nombreRol, "El nombre del rol es obligatorio");

        if (rolRepository.buscarPorNombre(nombreRol.trim()).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe un rol con ese nombre"
            );
        }

        rolRepository.insertarRol(nombreRol.trim());
    }

    public void actualizarRol(Integer idRol, String nombreRol) {

        validarId(idRol);
        validarTexto(nombreRol, "El nombre del rol es obligatorio");

        if (rolRepository.buscarPorId(idRol).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el rol indicado"
            );
        }

        rolRepository.actualizarRol(
                idRol,
                nombreRol.trim()
        );
    }

    public void eliminarRol(Integer idRol) {

        validarId(idRol);

        if (rolRepository.buscarPorId(idRol).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el rol indicado"
            );
        }

        rolRepository.eliminarRol(idRol);
    }

    private void validarId(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero"
            );
        }
    }

    private void validarTexto(String texto, String mensaje) {

        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}