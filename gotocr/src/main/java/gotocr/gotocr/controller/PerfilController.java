package gotocr.gotocr.controller;

import gotocr.gotocr.domain.Cliente;
import gotocr.gotocr.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final ClienteService clienteService;

    @GetMapping
    public String perfil(HttpSession session) {
        if (session.getAttribute("idCliente") == null) {
            return "redirect:/login";
        }
        return "perfil";
    }

    @GetMapping("/datos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> datosPerfil(HttpSession session) {
        Integer idCliente = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return clienteService.buscarPorId(idCliente)
                .map(cliente -> ResponseEntity.ok(clienteAJson(cliente)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            HttpSession session) {

        Integer idCliente
                = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return respuesta(
                    HttpStatus.UNAUTHORIZED,
                    false,
                    "Debe iniciar sesión."
            );
        }

        try {

            Cliente cliente
                    = clienteService.buscarPorId(idCliente)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                    "El cliente no existe."
                            )
                            );

            clienteService.actualizarCliente(
                    cliente.getIdCliente(),
                    cliente.getRol().getIdRol(),
                    nombre,
                    apellido,
                    correo,
                    cliente.getContrasena(),
                    cliente.getImagenPerfil(),
                    cliente.getTipoImagenPerfil(),
                    cliente.getTokenConfirmacion(),
                    cliente.getCorreoVerificado()
            );

            return respuesta(
                    HttpStatus.OK,
                    true,
                    "Perfil actualizado correctamente."
            );

        } catch (IllegalArgumentException e) {

            return respuesta(
                    HttpStatus.BAD_REQUEST,
                    false,
                    e.getMessage()
            );
        }
    }

    @PostMapping("/contrasena")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarContrasena(
            @RequestParam String contrasenaActual,
            @RequestParam String nuevaContrasena,
            @RequestParam String confirmarContrasena,
            HttpSession session) {

        Integer idCliente
                = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return respuesta(
                    HttpStatus.UNAUTHORIZED,
                    false,
                    "Debe iniciar sesión."
            );
        }

        try {

            Cliente cliente
                    = clienteService.buscarPorId(idCliente)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                    "El cliente no existe."
                            )
                            );

            if (!cliente.getContrasena().equals(contrasenaActual)) {
                throw new IllegalArgumentException(
                        "La contraseña actual es incorrecta."
                );
            }

            if (!nuevaContrasena.equals(confirmarContrasena)) {
                throw new IllegalArgumentException(
                        "Las nuevas contraseñas no coinciden."
                );
            }

            if (nuevaContrasena.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "La nueva contraseña es obligatoria."
                );
            }

            clienteService.actualizarCliente(
                    cliente.getIdCliente(),
                    cliente.getRol().getIdRol(),
                    cliente.getNombre(),
                    cliente.getApellido(),
                    cliente.getCorreo(),
                    nuevaContrasena,
                    cliente.getImagenPerfil(),
                    cliente.getTipoImagenPerfil(),
                    cliente.getTokenConfirmacion(),
                    cliente.getCorreoVerificado()
            );

            return respuesta(
                    HttpStatus.OK,
                    true,
                    "Contraseña actualizada correctamente."
            );

        } catch (IllegalArgumentException e) {

            return respuesta(
                    HttpStatus.BAD_REQUEST,
                    false,
                    e.getMessage()
            );
        }
    }

    private Map<String, Object> clienteAJson(Cliente cliente) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idCliente", cliente.getIdCliente());
        json.put("nombre", cliente.getNombre());
        json.put("apellido", cliente.getApellido());
        json.put("correo", cliente.getCorreo());
        json.put("imagenPerfil", cliente.getImagenPerfil());
        json.put("correoVerificado", cliente.getCorreoVerificado());
        json.put("fechaRegistro", cliente.getFechaRegistro());
        return json;
    }

    private ResponseEntity<Map<String, Object>> respuesta(
            HttpStatus estado,
            boolean ok,
            String mensaje) {

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("ok", ok);
        json.put("mensaje", mensaje);
        return ResponseEntity.status(estado).body(json);
    }
}
