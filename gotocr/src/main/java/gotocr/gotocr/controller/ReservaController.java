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
    // MOSTRAR VISTA DE RESERVA
    // =====================================================
    @GetMapping("/{idCuarto}")
    public String reserva(
            @PathVariable Integer idCuarto,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            @RequestParam(defaultValue = "1") Integer cantidadPersonas,
            Model model,
            HttpSession session) {

        Integer idCliente  = (Integer) session.getAttribute("idCliente");

        if (idCliente == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "idCuarto",
                idCuarto
        );

        model.addAttribute(
                "fechaEntrada",
                fechaEntrada
        );

        model.addAttribute(
                "fechaSalida",
                fechaSalida
        );

        model.addAttribute(
                "cantidadPersonas",
                cantidadPersonas
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


    @PostMapping("/confirmar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmarReserva(
            @RequestParam Integer idCuartoHotel,
            @RequestParam String fechaEntrada,
            @RequestParam String fechaSalida,
            @RequestParam Integer cantidadPersonas,
            @RequestParam String metodoPago,
            HttpSession session) {

        try {

            Integer idCliente
                    = (Integer) session.getAttribute(
                            "idCliente"
                    );

            if (idCliente == null) {

                return ResponseEntity
                        .status(401)
                        .body(
                                Map.of(
                                        "error",
                                        "Debe iniciar sesión."
                                )
                        );
            }

            CuartoHotel cuarto
                    = cuartoHotelService
                            .buscarPorId(idCuartoHotel)
                            .orElseThrow(()
                                    -> new IllegalArgumentException(
                                    "El cuarto seleccionado no existe."
                            )
                            );

            if (cuarto.getHotel() == null) {

                throw new IllegalArgumentException(
                        "El cuarto no tiene un hotel asociado."
                );
            }

            Integer idHotel
                    = cuarto.getHotel()
                            .getIdHotel();

            LocalDate entrada
                    = LocalDate.parse(
                            fechaEntrada
                    );

            LocalDate salida
                    = LocalDate.parse(
                            fechaSalida
                    );

            long noches
                    = java.time.temporal.ChronoUnit.DAYS
                            .between(
                                    entrada,
                                    salida
                            );

            if (noches <= 0) {

                throw new IllegalArgumentException(
                        "La fecha de salida debe ser posterior a la fecha de entrada."
                );
            }

            if (cantidadPersonas <= 0) {

                throw new IllegalArgumentException(
                        "La cantidad de personas debe ser mayor a cero."
                );
            }

            if (cuarto.getCantidadPersonas() != null
                    && cantidadPersonas > cuarto.getCantidadPersonas()) {

                throw new IllegalArgumentException(
                        "La cantidad de personas supera la capacidad del cuarto."
                );
            }

            BigDecimal precioNoche
                    = cuarto.getPrecioNoche();

            if (precioNoche == null) {

                throw new IllegalArgumentException(
                        "El cuarto no tiene un precio válido."
                );
            }

            BigDecimal precioTotal
                    = precioNoche.multiply(
                            BigDecimal.valueOf(
                                    noches
                            )
                    );

            String estadoReserva
                    = "CONFIRMADA";

            System.out.println(
                    "===== CONFIRMAR RESERVA ====="
            );

            System.out.println(
                    "Cliente: " + idCliente
            );

            System.out.println(
                    "Hotel: " + idHotel
            );

            System.out.println(
                    "Cuarto: " + idCuartoHotel
            );

            System.out.println(
                    "Entrada: " + entrada
            );

            System.out.println(
                    "Salida: " + salida
            );

            System.out.println(
                    "Personas: " + cantidadPersonas
            );

            System.out.println(
                    "Noches: " + noches
            );

            System.out.println(
                    "Precio total: " + precioTotal
            );

            System.out.println(
                    "Pago: " + metodoPago
            );

            Integer idReserva
                    = reservaService.insertarReserva(
                            idCliente,
                            idHotel,
                            idCuartoHotel,
                            entrada,
                            salida,
                            cantidadPersonas,
                            precioTotal,
                            estadoReserva,
                            metodoPago
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "idReserva",
                            idReserva,
                            "mensaje",
                            "Reserva realizada correctamente."
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage() != null
                                    ? e.getMessage()
                                    : "No fue posible realizar la reserva."
                            )
                    );
        }
    }
}
