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

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String correo,
            @RequestParam String contrasena,
            HttpSession session,
            Model model) {

        try {
            Cliente cliente = clienteService.buscarPorCorreo(correo)
                    .orElseThrow(() ->
                            new IllegalArgumentException("El correo ingresado no está registrado.")
                    );

            // Para el proyecto académico todavía se compara el texto recibido.
            // Cuando agreguen Spring Security, la contraseña debe almacenarse con hash.
            if (!cliente.getContrasena().equals(contrasena)) {
                throw new IllegalArgumentException("La contraseña es incorrecta.");
            }

            session.setAttribute("idCliente", cliente.getIdCliente());
            session.setAttribute("idRol", cliente.getRol().getIdRol());
            session.setAttribute("clienteSesion", cliente);

            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String confirmarContrasena,
            Model model) {

        try {
            if (!contrasena.equals(confirmarContrasena)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }

            Rol rolCliente = rolService.buscarPorNombre("CLIENTE")
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "No existe el rol CLIENTE. Debe registrarlo primero en la base de datos."
                            )
                    );

            clienteService.insertarCliente(
                    1,
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
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
