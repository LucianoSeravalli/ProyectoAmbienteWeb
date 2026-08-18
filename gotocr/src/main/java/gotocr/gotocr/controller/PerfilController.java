package gotocr.gotocr.controller;

import gotocr.gotocr.domain.Cliente;
import gotocr.gotocr.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final ClienteService clienteService;

    // ================================
    // MOSTRAR PERFIL
    // ================================

    @GetMapping
    public String mostrarPerfil(
            HttpSession session,
            Model model) {

        Integer idCliente =
                (Integer) session.getAttribute(
                        "idCliente"
                );

        if (idCliente == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService
                .buscarPorId(idCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El cliente no existe"
                        )
                );

        model.addAttribute(
                "cliente",
                cliente
        );

        return "perfil";
    }

    // ================================
    // ACTUALIZAR DATOS
    // ================================

    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            HttpSession session,
            Model model) {

        Integer idCliente =
                (Integer) session.getAttribute(
                        "idCliente"
                );

        if (idCliente == null) {
            return "redirect:/login";
        }

        try {

            Cliente cliente = clienteService
                    .buscarPorId(idCliente)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El cliente no existe"
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
                    cliente.getTokenConfirmacion(),
                    cliente.getCorreoVerificado()
            );

            session.setAttribute(
                    "clienteSesion",
                    clienteService
                            .buscarPorId(idCliente)
                            .orElse(null)
            );

            return "redirect:/perfil?actualizado";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "perfil";
        }
    }

    // ================================
    // CAMBIAR CONTRASEÑA
    // ================================

    @PostMapping("/contrasena")
    public String cambiarContrasena(
            @RequestParam String contrasenaActual,
            @RequestParam String nuevaContrasena,
            @RequestParam String confirmarContrasena,
            HttpSession session,
            Model model) {

        Integer idCliente =
                (Integer) session.getAttribute(
                        "idCliente"
                );

        if (idCliente == null) {
            return "redirect:/login";
        }

        try {

            Cliente cliente = clienteService
                    .buscarPorId(idCliente)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "El cliente no existe"
                            )
                    );

            if (!cliente.getContrasena()
                    .equals(contrasenaActual)) {

                throw new IllegalArgumentException(
                        "La contraseña actual es incorrecta"
                );
            }

            if (!nuevaContrasena
                    .equals(confirmarContrasena)) {

                throw new IllegalArgumentException(
                        "Las nuevas contraseñas no coinciden"
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
                    cliente.getTokenConfirmacion(),
                    cliente.getCorreoVerificado()
            );

            return "redirect:/perfil?contrasenaActualizada";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "cliente",
                    clienteService
                            .buscarPorId(idCliente)
                            .orElse(null)
            );

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "perfil";
        }
    }
}