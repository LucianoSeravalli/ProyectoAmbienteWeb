package gotocr.gotocr.controller;


import gotocr.gotocr.domain.Cliente;
import gotocr.gotocr.domain.Reserva;
import gotocr.gotocr.service.ClienteService;
import gotocr.gotocr.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final ReservaService reservaService;
    private final ClienteService clienteService;

    // ================================
    // TODAS LAS RESERVAS
    // ================================

    @GetMapping
    public String mostrarHistorial(
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
                                "Cliente no encontrado"
                        )
                );

        List<Reserva> reservas =
                reservaService.buscarPorCliente(
                        idCliente
                );

        model.addAttribute(
                "cliente",
                cliente
        );

        model.addAttribute(
                "reservas",
                reservas
        );

        return "historial";
    }

    // ================================
    // FILTRAR POR ESTADO
    // ================================

    @GetMapping("/estado/{estado}")
    public String filtrarPorEstado(
            @PathVariable String estado,
            HttpSession session,
            Model model) {

        Integer idCliente =
                (Integer) session.getAttribute(
                        "idCliente"
                );

        if (idCliente == null) {
            return "redirect:/login";
        }

        List<Reserva> reservas =
                reservaService.buscarPorCliente(
                        idCliente
                )
                .stream()
                .filter(reserva ->
                        reserva.getEstadoReserva()
                                .equalsIgnoreCase(estado)
                )
                .toList();

        model.addAttribute(
                "cliente",
                clienteService.buscarPorId(
                        idCliente
                ).orElse(null)
        );

        model.addAttribute(
                "reservas",
                reservas
        );

        model.addAttribute(
                "estadoSeleccionado",
                estado
        );

        return "historial";
    }
}