package com.gotocr.controller;

import com.gotocr.domain.CuartoHotel;
import com.gotocr.domain.Hotel;
import com.gotocr.service.CuartoHotelService;
import com.gotocr.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HotelService hotelService;
    private final CuartoHotelService cuartoHotelService;

    @GetMapping({"/", "/index"})
    public String inicio() {
        return "index";
    }

    @GetMapping("/inicio/datos/hoteles-destacados")
    @ResponseBody
    public List<Map<String, Object>> hotelesDestacados() {
        return hotelService.listarHoteles()
                .stream()
                .filter(h -> h.getCalificacionPromedio() != null)
                .sorted(Comparator.comparing(Hotel::getCalificacionPromedio).reversed())
                .limit(3)
                .map(this::hotelAJson)
                .toList();
    }

    private Map<String, Object> hotelAJson(Hotel hotel) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idHotel", hotel.getIdHotel());
        json.put("nombre", hotel.getNombre());
        json.put("imagenPrincipal", hotel.getImagenPrincipal());
        json.put("provincia", hotel.getProvincia());
        json.put("canton", hotel.getCanton());
        json.put("calificacionPromedio", hotel.getCalificacionPromedio());
        json.put("cuartosDisponibles", hotel.getCuartosDisponibles());

        List<CuartoHotel> cuartos = cuartoHotelService.buscarPorHotel(hotel.getIdHotel());
        BigDecimal precioMinimo = cuartos.stream()
                .filter(c -> c.getPrecioNoche() != null)
                .map(CuartoHotel::getPrecioNoche)
                .min(BigDecimal::compareTo)
                .orElse(null);

        json.put("precioMinimo", precioMinimo);
        return json;
    }
}
