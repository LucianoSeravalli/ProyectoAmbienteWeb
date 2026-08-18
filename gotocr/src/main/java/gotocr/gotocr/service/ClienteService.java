package gotocr.gotocr.service;
import gotocr.gotocr.domain.Cliente;
import gotocr.gotocr.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public List<Cliente> listarClientes() {
        return clienteRepository.listarClientes();
    }

    public Optional<Cliente> buscarPorId(Integer idCliente) {
        validarId(idCliente);
        return clienteRepository.buscarPorId(idCliente);
    }

    public Optional<Cliente> buscarPorCorreo(String correo) {
        validarCorreo(correo);
        return clienteRepository.buscarPorCorreo(correo.trim());
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        validarTexto(nombre, "El nombre es obligatorio");
        return clienteRepository.buscarPorNombre(nombre.trim());
    }

    public List<Cliente> buscarPorRol(Integer idRol) {
        validarId(idRol);
        return clienteRepository.buscarPorRol(idRol);
    }

    public void insertarCliente(
            Integer idRol,
            String nombre,
            String apellido,
            String correo,
            String contrasena,
            String imagenPerfil,
            String tokenConfirmacion,
            Boolean correoVerificado) {

        validarId(idRol);

        validarTexto(nombre, "El nombre es obligatorio");
        validarTexto(apellido, "El apellido es obligatorio");
        validarCorreo(correo);
        validarTexto(contrasena, "La contraseña es obligatoria");

        if (clienteRepository.buscarPorCorreo(correo.trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese correo");
        }

        clienteRepository.insertarCliente(
                idRol,
                nombre.trim(),
                apellido.trim(),
                correo.trim(),
                contrasena,
                imagenPerfil,
                tokenConfirmacion,
                correoVerificado
        );
    }

    public void actualizarCliente(
            Integer idCliente,
            Integer idRol,
            String nombre,
            String apellido,
            String correo,
            String contrasena,
            String imagenPerfil,
            String tokenConfirmacion,
            Boolean correoVerificado) {

        validarId(idCliente);
        validarId(idRol);

        validarTexto(nombre, "El nombre es obligatorio");
        validarTexto(apellido, "El apellido es obligatorio");
        validarCorreo(correo);
        validarTexto(contrasena, "La contraseña es obligatoria");

        if (clienteRepository.buscarPorId(idCliente).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el cliente indicado"
            );
        }

        clienteRepository.actualizarCliente(
                idCliente,
                idRol,
                nombre.trim(),
                apellido.trim(),
                correo.trim(),
                contrasena,
                imagenPerfil,
                tokenConfirmacion,
                correoVerificado
        );
    }

    public void eliminarCliente(Integer idCliente) {

        validarId(idCliente);

        if (clienteRepository.buscarPorId(idCliente).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el cliente indicado"
            );
        }

        clienteRepository.eliminarCliente(idCliente);
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

    private void validarCorreo(String correo) {

        validarTexto(correo, "El correo es obligatorio");

        if (!EMAIL_PATTERN.matcher(correo.trim()).matches()) {
            throw new IllegalArgumentException(
                    "El correo no tiene un formato válido"
            );
        }
    }
}