package gotocr.gotocr.controller;

import gotocr.gotocr.domain.Hotel;
import gotocr.gotocr.service.CuartoHotelService;
import gotocr.gotocr.service.HotelService;
import gotocr.gotocr.service.ResenaHotelService;
import gotocr.gotocr.service.TipoCuartoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/hoteles")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final CuartoHotelService cuartoHotelService;
    private final TipoCuartoService tipoCuartoService;
    private final ResenaHotelService resenaHotelService;

    // ================================
    // LISTADO DE HOTELES
    // ================================

    @GetMapping
    public String listarHoteles(Model model) {

        model.addAttribute(
                "hoteles",
                hotelService.listarHoteles()
        );

        model.addAttribute(
                "tiposCuarto",
                tipoCuartoService.listarTiposCuarto()
        );

        return "hoteles";
    }

    // ================================
    // FILTRO POR PROVINCIA
    // ================================

    @GetMapping("/provincia")
    public String buscarPorProvincia(
            @RequestParam String provincia,
            Model model) {

        model.addAttribute(
                "hoteles",
                hotelService.buscarPorProvincia(provincia)
        );

        model.addAttribute(
                "tiposCuarto",
                tipoCuartoService.listarTiposCuarto()
        );

        return "hoteles";
    }

    // ================================
    // FILTRO POR NOMBRE
    // ================================

    @GetMapping("/buscar")
    public String buscarPorNombre(
            @RequestParam String nombre,
            Model model) {

        model.addAttribute(
                "hoteles",
                hotelService.buscarPorNombre(nombre)
        );

        model.addAttribute(
                "tiposCuarto",
                tipoCuartoService.listarTiposCuarto()
        );

        return "hoteles";
    }

    // ================================
    // CALIFICACIÓN
    // ================================

    @GetMapping("/calificacion")
    public String buscarPorCalificacion(
            @RequestParam BigDecimal calificacion,
            Model model) {

        model.addAttribute(
                "hoteles",
                hotelService.buscarPorCalificacionMinima(
                        calificacion
                )
        );

        model.addAttribute(
                "tiposCuarto",
                tipoCuartoService.listarTiposCuarto()
        );

        return "hoteles";
    }

    // ================================
    // DETALLE DEL HOTEL
    // ================================

    @GetMapping("/{idHotel}")
    public String detalleHotel(
            @PathVariable Integer idHotel,
            Model model) {

        Hotel hotel = hotelService.buscarPorId(idHotel)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El hotel solicitado no existe"
                        )
                );

        model.addAttribute(
                "hotel",
                hotel
        );

        model.addAttribute(
                "cuartos",
                cuartoHotelService.buscarPorHotel(idHotel)
        );

        model.addAttribute(
                "resenas",
                resenaHotelService.buscarPorHotel(idHotel)
        );

        return "hotel-detalle";
    }
}