
package gotocr.gotocr.controller;

import gotocr.gotocr.domain.Cliente;
import gotocr.gotocr.domain.Rol;
import gotocr.gotocr.service.ClienteService;
import gotocr.gotocr.service.RolService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final ClienteService clienteService;
    private final RolService rolService;

    // ================================
    // MOSTRAR LOGIN
    // ================================

    @GetMapping("/login")
    public String mostrarLogin() {

        return "login";
    }

    // ================================
    // PROCESAR LOGIN
    // ================================

    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam String correo,
            @RequestParam String contrasena,
            HttpSession session,
            Model model) {

        try {

            Cliente cliente = clienteService
                    .buscarPorCorreo(correo)
                    .orElse(null);

            if (cliente == null) {

                model.addAttribute(
                        "error",
                        "El correo ingresado no está registrado"
                );

                return "login";
            }

            if (!cliente.getContrasena().equals(contrasena)) {

                model.addAttribute(
                        "error",
                        "La contraseña es incorrecta"
                );

                return "login";
            }

            if (!Boolean.TRUE.equals(
                    cliente.getCorreoVerificado()
            )) {

                model.addAttribute(
                        "error",
                        "Debe verificar su correo electrónico"
                );

                return "login";
            }

            session.setAttribute(
                    "clienteSesion",
                    cliente
            );

            session.setAttribute(
                    "idCliente",
                    cliente.getIdCliente()
            );

            return "redirect:/";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "login";
        }
    }

    // ================================
    // MOSTRAR REGISTRO
    // ================================

    @GetMapping("/registro")
    public String mostrarRegistro() {

        return "registro";
    }

    // ================================
    // REGISTRAR CLIENTE
    // ================================

    @PostMapping("/registro")
    public String registrarCliente(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String confirmarContrasena,
            Model model) {

        try {

            if (!contrasena.equals(confirmarContrasena)) {

                model.addAttribute(
                        "error",
                        "Las contraseñas no coinciden"
                );

                return "registro";
            }

            Rol rolCliente = rolService
                    .buscarPorNombre("CLIENTE")
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "No existe el rol CLIENTE"
                            )
                    );

            clienteService.insertarCliente(
                    rolCliente.getIdRol(),
                    nombre,
                    apellido,
                    correo,
                    contrasena,
                    null,
                    null,
                    false
            );

            return "redirect:/login?registroExitoso";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "registro";
        }
    }

    // ================================
    // CERRAR SESIÓN
    // ================================

    @GetMapping("/logout")
    public String cerrarSesion(
            HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}