package com.gotocr.controller;

import com.gotocr.domain.Cliente;
import com.gotocr.domain.CuartoHotel;
import com.gotocr.service.ClienteService;
import com.gotocr.service.CuartoHotelService;
import com.gotocr.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final CuartoHotelService cuartoHotelService;
    private final ClienteService clienteService;

    @GetMapping("/{idCuarto}")
    public String reserva(
            @PathVariable Integer idCuarto,
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas,
            HttpSession session,
            Model model) {

        if (session.getAttribute("idCliente") == null) {
            return "redirect:/login";
        }

        model.addAttribute("idCuarto", idCuarto);
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        model.addAttribute("cantidadPersonas", cantidadPersonas);

        return "reserva";
    }

    @GetMapping("/datos/{idCuarto}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> datosReserva(
            @PathVariable Integer idCuarto,
            HttpSession session) {

        Integer idCliente = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Cliente cliente = clienteService.buscarPorId(idCliente).orElse(null);
        CuartoHotel cuarto = cuartoHotelService.buscarPorId(idCuarto).orElse(null);

        if (cliente == null || cuarto == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("cliente", clienteAJson(cliente));
        json.put("cuarto", cuartoAJson(cuarto));
        json.put("hotel", hotelAJson(cuarto));

        return ResponseEntity.ok(json);
    }

    @PostMapping("/confirmar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmarReserva(
            @RequestParam Integer idCuartoHotel,
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas,
            @RequestParam String metodoPago,
            HttpSession session) {

        Integer idCliente = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return respuesta(HttpStatus.UNAUTHORIZED, false, "Debe iniciar sesión.");
        }

        try {
            CuartoHotel cuarto = cuartoHotelService.buscarPorId(idCuartoHotel)
                    .orElseThrow(() ->
                            new IllegalArgumentException("El cuarto seleccionado no existe.")
                    );

            if (!"DISPONIBLE".equalsIgnoreCase(cuarto.getEstado())) {
                throw new IllegalArgumentException("El cuarto seleccionado no está disponible.");
            }

            if (cantidadPersonas > cuarto.getCantidadPersonas()) {
                throw new IllegalArgumentException(
                        "La cantidad de huéspedes supera la capacidad del cuarto."
                );
            }

            if (!fechaSalida.isAfter(fechaEntrada)) {
                throw new IllegalArgumentException(
                        "La fecha de salida debe ser posterior a la fecha de entrada."
                );
            }

            long noches = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);

            BigDecimal subtotal = cuarto.getPrecioNoche()
                    .multiply(BigDecimal.valueOf(noches));

            BigDecimal precioTotal = subtotal
                    .multiply(new BigDecimal("1.12"))
                    .setScale(2, RoundingMode.HALF_UP);

            reservaService.insertarReserva(
                    idCliente,
                    cuarto.getHotel().getIdHotel(),
                    cuarto.getIdCuartoHotel(),
                    fechaEntrada,
                    fechaSalida,
                    cantidadPersonas,
                    precioTotal,
                    "CONFIRMADA"
            );

            /*
             * metodoPago ya llega desde JavaScript.
             * Falta enlazar PAGO cuando sp_insert_reserva devuelva el idReserva generado.
             */
            return respuesta(HttpStatus.OK, true, "Reserva registrada correctamente.");

        } catch (IllegalArgumentException e) {
            return respuesta(HttpStatus.BAD_REQUEST, false, e.getMessage());
        }
    }

    private Map<String, Object> clienteAJson(Cliente cliente) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("nombre", cliente.getNombre());
        json.put("apellido", cliente.getApellido());
        json.put("correo", cliente.getCorreo());
        return json;
    }

    private Map<String, Object> cuartoAJson(CuartoHotel cuarto) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idCuartoHotel", cuarto.getIdCuartoHotel());
        json.put("numeroCuarto", cuarto.getNumeroCuarto());
        json.put("cantidadPersonas", cuarto.getCantidadPersonas());
        json.put("precioNoche", cuarto.getPrecioNoche());
        json.put("estado", cuarto.getEstado());
        json.put(
                "tipoCuarto",
                cuarto.getTipoCuarto() != null
                        ? cuarto.getTipoCuarto().getNombreTipo()
                        : "Cuarto"
        );
        return json;
    }

    private Map<String, Object> hotelAJson(CuartoHotel cuarto) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idHotel", cuarto.getHotel().getIdHotel());
        json.put("nombre", cuarto.getHotel().getNombre());
        json.put("canton", cuarto.getHotel().getCanton());
        json.put("provincia", cuarto.getHotel().getProvincia());
        json.put("imagenPrincipal", cuarto.getHotel().getImagenPrincipal());
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
