package com.gotocr.controller;

import com.gotocr.domain.CuartoHotel;
import com.gotocr.domain.Hotel;
import com.gotocr.domain.ImagenCuarto;
import com.gotocr.domain.ResenaHotel;
import com.gotocr.service.CuartoHotelService;
import com.gotocr.service.HotelService;
import com.gotocr.service.ResenaHotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hoteles")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final CuartoHotelService cuartoHotelService;
    private final ResenaHotelService resenaHotelService;

    @GetMapping
    public String hoteles() {
        return "hoteles";
    }

    @GetMapping("/{idHotel}")
    public String detalle(@PathVariable Integer idHotel, Model model) {
        model.addAttribute("idHotel", idHotel);
        return "hotel-detalle";
    }

    @GetMapping("/datos")
    @ResponseBody
    public List<Map<String, Object>> obtenerHoteles() {
        return hotelService.listarHoteles()
                .stream()
                .map(this::hotelCatalogoAJson)
                .toList();
    }

    @GetMapping("/datos/{idHotel}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerHotel(@PathVariable Integer idHotel) {
        return hotelService.buscarPorId(idHotel)
                .map(hotel -> ResponseEntity.ok(hotelDetalleAJson(hotel)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/datos/{idHotel}/cuartos")
    @ResponseBody
    public List<Map<String, Object>> obtenerCuartos(@PathVariable Integer idHotel) {
        return cuartoHotelService.buscarPorHotel(idHotel)
                .stream()
                .map(this::cuartoAJson)
                .toList();
    }

    @GetMapping("/datos/{idHotel}/resenas")
    @ResponseBody
    public List<Map<String, Object>> obtenerResenas(@PathVariable Integer idHotel) {
        return resenaHotelService.buscarPorHotel(idHotel)
                .stream()
                .map(this::resenaAJson)
                .toList();
    }

    private Map<String, Object> hotelCatalogoAJson(Hotel hotel) {
        Map<String, Object> json = hotelDetalleAJson(hotel);

        List<CuartoHotel> cuartos = cuartoHotelService.buscarPorHotel(hotel.getIdHotel());

        BigDecimal precioMinimo = cuartos.stream()
                .filter(c -> c.getPrecioNoche() != null)
                .map(CuartoHotel::getPrecioNoche)
                .min(BigDecimal::compareTo)
                .orElse(null);

        List<String> tiposCuarto = cuartos.stream()
                .filter(c -> c.getTipoCuarto() != null)
                .map(c -> c.getTipoCuarto().getNombreTipo())
                .distinct()
                .toList();

        json.put("precioMinimo", precioMinimo);
        json.put("tiposCuarto", tiposCuarto);

        return json;
    }

    private Map<String, Object> hotelDetalleAJson(Hotel hotel) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idHotel", hotel.getIdHotel());
        json.put("nombre", hotel.getNombre());
        json.put("descripcion", hotel.getDescripcion());
        json.put("imagenPrincipal", hotel.getImagenPrincipal());
        json.put("provincia", hotel.getProvincia());
        json.put("canton", hotel.getCanton());
        json.put("direccion", hotel.getDireccion());
        json.put("telefono", hotel.getTelefono());
        json.put("calificacionPromedio", hotel.getCalificacionPromedio());
        json.put("cuartosDisponibles", hotel.getCuartosDisponibles());
        json.put("estado", hotel.getEstado());
        return json;
    }

    private Map<String, Object> cuartoAJson(CuartoHotel cuarto) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idCuartoHotel", cuarto.getIdCuartoHotel());
        json.put("numeroCuarto", cuarto.getNumeroCuarto());
        json.put("cantidadPersonas", cuarto.getCantidadPersonas());
        json.put("precioNoche", cuarto.getPrecioNoche());
        json.put("estado", cuarto.getEstado());

        if (cuarto.getTipoCuarto() != null) {
            json.put("idTipoCuarto", cuarto.getTipoCuarto().getIdTipoCuarto());
            json.put("tipoCuarto", cuarto.getTipoCuarto().getNombreTipo());
        }

        String imagen = null;
        if (cuarto.getImagenes() != null && !cuarto.getImagenes().isEmpty()) {
            ImagenCuarto primera = cuarto.getImagenes().getFirst();
            imagen = primera.getUrlImagen();
        }
        json.put("imagen", imagen);

        return json;
    }

    private Map<String, Object> resenaAJson(ResenaHotel resena) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idResena", resena.getIdResena());
        json.put("calificacion", resena.getCalificacion());
        json.put("comentario", resena.getComentario());
        json.put("fecha", resena.getFecha());

        if (resena.getCliente() != null) {
            json.put(
                    "cliente",
                    resena.getCliente().getNombre() + " " + resena.getCliente().getApellido()
            );
        } else {
            json.put("cliente", "Cliente");
        }

        return json;
    }
}
