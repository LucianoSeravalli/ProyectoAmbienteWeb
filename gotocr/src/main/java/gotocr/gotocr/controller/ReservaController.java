package gotocr.gotocr.controller;

import gotocr.gotocr.domain.Cliente;
import gotocr.gotocr.domain.CuartoHotel;
import gotocr.gotocr.service.ClienteService;
import gotocr.gotocr.service.CuartoHotelService;
import gotocr.gotocr.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final CuartoHotelService cuartoHotelService;
    private final ClienteService clienteService;

    // =====================================================
    // MOSTRAR VISTA
    // =====================================================
    @GetMapping("/{idCuarto}")
    public String mostrarReserva(
            @PathVariable Integer idCuarto,
            Model model,
            HttpSession session) {

        Integer idCliente
                = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "idCuarto",
                idCuarto
        );

        return "reserva";
    }

    // =====================================================
    // DATOS PARA JAVASCRIPT
    // =====================================================
    @GetMapping("/datos/{idCuarto}")
    @ResponseBody
    public ResponseEntity<?> obtenerDatosReserva(
            @PathVariable Integer idCuarto,
            HttpSession session) {

        Integer idCliente
                = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of(
                            "error",
                            "Debe iniciar sesión"
                    ));
        }

        try {

            CuartoHotel cuarto
                    = cuartoHotelService
                            .buscarPorId(idCuarto)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                    "El cuarto no existe"
                            )
                            );

            Cliente cliente
                    = clienteService
                            .buscarPorId(idCliente)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                    "El cliente no existe"
                            )
                            );

            Map<String, Object> respuesta
                    = new HashMap<>();

            respuesta.put(
                    "idCuartoHotel",
                    cuarto.getIdCuartoHotel()
            );

            respuesta.put(
                    "numeroCuarto",
                    cuarto.getNumeroCuarto()
            );

            respuesta.put(
                    "cantidadPersonas",
                    cuarto.getCantidadPersonas()
            );

            respuesta.put(
                    "precioNoche",
                    cuarto.getPrecioNoche()
            );

            respuesta.put(
                    "estado",
                    cuarto.getEstado()
            );

            respuesta.put(
                    "idHotel",
                    cuarto.getHotel().getIdHotel()
            );

            respuesta.put(
                    "nombreHotel",
                    cuarto.getHotel().getNombre()
            );

            respuesta.put(
                    "provincia",
                    cuarto.getHotel().getProvincia()
            );

            respuesta.put(
                    "canton",
                    cuarto.getHotel().getCanton()
            );

            respuesta.put(
                    "nombreCliente",
                    cliente.getNombre()
            );

            respuesta.put(
                    "apellidoCliente",
                    cliente.getApellido()
            );

            respuesta.put(
                    "correoCliente",
                    cliente.getCorreo()
            );

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    // =====================================================
    // CONFIRMAR RESERVA DESDE JAVASCRIPT
    // =====================================================
    @PostMapping("/confirmar")
    @ResponseBody
    public ResponseEntity<?> confirmarReserva(
            @RequestParam Integer idCuartoHotel,
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas,
            @RequestParam String metodoPago,
            HttpSession session) {

        Integer idCliente
                = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {

            return ResponseEntity
                    .status(401)
                    .body(Map.of(
                            "error",
                            "Debe iniciar sesión"
                    ));
        }

        try {

            CuartoHotel cuarto
                    = cuartoHotelService
                            .buscarPorId(idCuartoHotel)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                    "El cuarto seleccionado no existe"
                            )
                            );

            // =============================
            // DISPONIBILIDAD
            // =============================
            if (!"DISPONIBLE".equalsIgnoreCase(
                    cuarto.getEstado())) {

                throw new IllegalArgumentException(
                        "El cuarto no se encuentra disponible"
                );
            }

            // =============================
            // CAPACIDAD
            // =============================
            if (cantidadPersonas
                    > cuarto.getCantidadPersonas()) {

                throw new IllegalArgumentException(
                        "La cantidad de huéspedes supera la capacidad del cuarto"
                );
            }

            // =============================
            // FECHAS
            // =============================
            if (fechaEntrada == null
                    || fechaSalida == null) {

                throw new IllegalArgumentException(
                        "Debe indicar las fechas de entrada y salida"
                );
            }

            if (!fechaSalida.isAfter(fechaEntrada)) {

                throw new IllegalArgumentException(
                        "La fecha de salida debe ser posterior a la fecha de entrada"
                );
            }

            long noches
                    = ChronoUnit.DAYS.between(
                            fechaEntrada,
                            fechaSalida
                    );

            // =============================
            // PRECIO REAL DESDE LA BD
            // =============================
            BigDecimal precioTotal
                    = cuarto.getPrecioNoche()
                            .multiply(
                                    BigDecimal.valueOf(noches)
                            );

            Integer idReserva
                    = reservaService.confirmarReserva(
                            idCliente,
                            cuarto.getHotel().getIdHotel(),
                            cuarto.getIdCuartoHotel(),
                            fechaEntrada,
                            fechaSalida,
                            cantidadPersonas,
                            precioTotal,
                            metodoPago
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Reserva realizada correctamente",
                            "idReserva",
                            idReserva
                    )
            );

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );
        }
    }
}
