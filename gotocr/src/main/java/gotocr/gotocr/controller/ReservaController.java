package gotocr.gotocr.controller;


import gotocr.gotocr.domain.Cliente;
import gotocr.gotocr.domain.CuartoHotel;
import gotocr.gotocr.service.ClienteService;
import gotocr.gotocr.service.CuartoHotelService;
import gotocr.gotocr.service.PagoService;
import gotocr.gotocr.service.ReservaService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/reserva")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final CuartoHotelService cuartoHotelService;
    private final ClienteService clienteService;
    private final PagoService pagoService;

    // ================================
    // MOSTRAR PÁGINA DE RESERVA
    // ================================

    @GetMapping("/{idCuarto}")
    public String mostrarReserva(
            @PathVariable Integer idCuarto,
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas,
            HttpSession session,
            Model model) {

        Integer idCliente =
                (Integer) session.getAttribute(
                        "idCliente"
                );

        if (idCliente == null) {
            return "redirect:/login";
        }

        CuartoHotel cuarto =
                cuartoHotelService.buscarPorId(
                        idCuarto
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "El cuarto seleccionado no existe"
                        )
                );

        Cliente cliente =
                clienteService.buscarPorId(
                        idCliente
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente no encontrado"
                        )
                );

        long noches = ChronoUnit.DAYS.between(
                fechaEntrada,
                fechaSalida
        );

        BigDecimal subtotal =
                cuarto.getPrecioNoche()
                        .multiply(
                                BigDecimal.valueOf(noches)
                        );

        model.addAttribute("cliente", cliente);
        model.addAttribute("cuarto", cuarto);
        model.addAttribute("hotel", cuarto.getHotel());
        model.addAttribute("fechaEntrada", fechaEntrada);
        model.addAttribute("fechaSalida", fechaSalida);
        model.addAttribute("cantidadPersonas", cantidadPersonas);
        model.addAttribute("cantidadNoches", noches);
        model.addAttribute("precioTotal", subtotal);

        return "reserva";
    }

    // ================================
    // CONFIRMAR RESERVA
    // ================================

    @PostMapping("/confirmar")
    public String confirmarReserva(
            @RequestParam Integer idCuartoHotel,
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida,
            @RequestParam Integer cantidadPersonas,
            @RequestParam String metodoPago,
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

            CuartoHotel cuarto =
                    cuartoHotelService
                            .buscarPorId(idCuartoHotel)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "El cuarto seleccionado no existe"
                                    )
                            );

            if (!cuarto.getEstado()
                    .equalsIgnoreCase("DISPONIBLE")) {

                throw new IllegalArgumentException(
                        "El cuarto seleccionado no está disponible"
                );
            }

            if (cantidadPersonas >
                    cuarto.getCantidadPersonas()) {

                throw new IllegalArgumentException(
                        "La cantidad de huéspedes supera la capacidad del cuarto"
                );
            }

            long noches =
                    ChronoUnit.DAYS.between(
                            fechaEntrada,
                            fechaSalida
                    );

            BigDecimal precioTotal =
                    cuarto.getPrecioNoche()
                            .multiply(
                                    BigDecimal.valueOf(
                                            noches
                                    )
                            );

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
             * Como nuestro SP actual de reserva
             * no devuelve el idReserva generado,
             * todavía no podemos insertar el pago
             * aquí correctamente.
             *
             * Lo corregiremos haciendo que
             * sp_insert_reserva devuelva LAST_INSERT_ID().
             */

            return "redirect:/historial?reservaExitosa";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "reserva";
        }
    }
}