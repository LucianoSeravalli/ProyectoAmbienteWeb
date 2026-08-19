package gotocr.gotocr.controller;

import gotocr.gotocr.service.ClienteService;
import gotocr.gotocr.domain.Cliente;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
public class SessionController {

    private final ClienteService clienteService;

    @GetMapping("/sesion/datos")
    @ResponseBody
    public ResponseEntity<?> obtenerSesion(
            HttpSession session) {

        Integer idCliente
                = (Integer) session.getAttribute(
                        "idCliente"
                );

        Integer idRol
                = (Integer) session.getAttribute(
                        "idRol"
                );

        if (idCliente == null) {

            return ResponseEntity.ok(
                    Map.of(
                            "autenticado",
                            false
                    )
            );
        }

        Cliente cliente
                = clienteService
                        .buscarPorId(idCliente)
                        .orElse(null);

        if (cliente == null) {

            return ResponseEntity.ok(
                    Map.of(
                            "autenticado",
                            false
                    )
            );
        }

        Map<String, Object> datos
                = new HashMap<>();

        datos.put(
                "autenticado",
                true
        );

        datos.put(
                "idCliente",
                cliente.getIdCliente()
        );

        datos.put(
                "idRol",
                idRol
        );

        datos.put(
                "esAdmin",
                idRol != null && idRol == 2
        );

        datos.put(
                "nombre",
                cliente.getNombre()
        );

        datos.put(
                "apellido",
                cliente.getApellido()
        );

        datos.put(
                "tieneImagenPerfil",
                cliente.getImagenPerfil() != null
                && cliente.getImagenPerfil().length > 0
        );

        return ResponseEntity.ok(datos);
    }
}
