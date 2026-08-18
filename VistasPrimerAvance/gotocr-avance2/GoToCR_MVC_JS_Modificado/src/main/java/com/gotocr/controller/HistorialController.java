package com.gotocr.controller;

import com.gotocr.domain.Cliente;
import com.gotocr.domain.Reserva;
import com.gotocr.service.ClienteService;
import com.gotocr.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final ReservaService reservaService;
    private final ClienteService clienteService;

    @GetMapping
    public String historial(HttpSession session) {
        if (session.getAttribute("idCliente") == null) {
            return "redirect:/login";
        }
        return "historial";
    }

    @GetMapping("/datos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> datosHistorial(HttpSession session) {
        Integer idCliente = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cliente cliente = clienteService.buscarPorId(idCliente).orElse(null);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> reservas = reservaService.buscarPorCliente(idCliente)
                .stream()
                .map(this::reservaAJson)
                .toList();

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("cliente", clienteAJson(cliente));
        json.put("reservas", reservas);

        return ResponseEntity.ok(json);
    }

    private Map<String, Object> clienteAJson(Cliente cliente) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("nombre", cliente.getNombre());
        json.put("apellido", cliente.getApellido());
        json.put("correo", cliente.getCorreo());
        json.put("imagenPerfil", cliente.getImagenPerfil());
        return json;
    }

    private Map<String, Object> reservaAJson(Reserva reserva) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idReserva", reserva.getIdReserva());
        json.put("fechaEntrada", reserva.getFechaEntrada());
        json.put("fechaSalida", reserva.getFechaSalida());
        json.put("cantidadPersonas", reserva.getCantidadPersonas());
        json.put("precioTotal", reserva.getPrecioTotal());
        json.put("estadoReserva", reserva.getEstadoReserva());

        if (reserva.getHotel() != null) {
            json.put("idHotel", reserva.getHotel().getIdHotel());
            json.put("hotel", reserva.getHotel().getNombre());
            json.put("imagenHotel", reserva.getHotel().getImagenPrincipal());
        }

        if (reserva.getCuartoHotel() != null && reserva.getCuartoHotel().getTipoCuarto() != null) {
            json.put("tipoCuarto", reserva.getCuartoHotel().getTipoCuarto().getNombreTipo());
        }

        return json;
    }
}
