package gotocr.gotocr.controller;

import gotocr.gotocr.domain.Hotel;
import gotocr.gotocr.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HotelService hotelService;

    @GetMapping
    public String dashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/resumen")
    @ResponseBody
    public Map<String, Object> resumen() {

        List<Hotel> hoteles = hotelService.listarHoteles();

        long hotelesActivos = hoteles.stream()
                .filter(h -> h.getEstado() != null
                        && h.getEstado().equalsIgnoreCase("ACTIVO"))
                .count();

        int cuartosDisponibles = hoteles.stream()
                .map(Hotel::getCuartosDisponibles)
                .filter(valor -> valor != null)
                .mapToInt(Integer::intValue)
                .sum();

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("totalHoteles", hoteles.size());
        json.put("hotelesActivos", hotelesActivos);
        json.put("cuartosDisponibles", cuartosDisponibles);

        return json;
    }

    @GetMapping("/hoteles")
    @ResponseBody
    public List<Map<String, Object>> listarHoteles() {

        return hotelService.listarHoteles()
                .stream()
                .map(this::hotelAJson)
                .toList();
    }

    @GetMapping("/hoteles/{idHotel}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarHotel(
            @PathVariable Integer idHotel) {

        return hotelService.buscarPorId(idHotel)
                .map(hotel -> ResponseEntity.ok(hotelAJson(hotel)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/hoteles/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarHotel(
            @RequestParam(required = false) Integer idHotel,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String imagenPrincipal,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String canton,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) BigDecimal calificacionPromedio,
            @RequestParam(required = false) Integer cuartosDisponibles,
            @RequestParam String estado) {

        try {

            BigDecimal calificacion = calificacionPromedio == null
                    ? BigDecimal.ZERO
                    : calificacionPromedio;

            Integer cuartos = cuartosDisponibles == null
                    ? 0
                    : cuartosDisponibles;

            if (idHotel == null) {

                hotelService.insertarHotel(
                        nombre,
                        limpiar(descripcion),
                        limpiar(imagenPrincipal),
                        limpiar(provincia),
                        limpiar(canton),
                        limpiar(direccion),
                        limpiar(telefono),
                        calificacion,
                        cuartos,
                        estado
                );

                return ResponseEntity.ok(
                        Map.of(
                                "mensaje",
                                "Hotel registrado correctamente"
                        )
                );
            }

            hotelService.actualizarHotel(
                    idHotel,
                    nombre,
                    limpiar(descripcion),
                    limpiar(imagenPrincipal),
                    limpiar(provincia),
                    limpiar(canton),
                    limpiar(direccion),
                    limpiar(telefono),
                    calificacion,
                    cuartos,
                    estado
            );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Hotel actualizado correctamente"
                    )
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/hoteles/eliminar/{idHotel}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarHotel(
            @PathVariable Integer idHotel) {

        try {

            hotelService.eliminarHotel(idHotel);

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Hotel eliminado correctamente"
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            "No fue posible eliminar el hotel. "
                                    + "Puede tener registros relacionados."
                    ));
        }
    }

    private Map<String, Object> hotelAJson(Hotel hotel) {

        Map<String, Object> json = new LinkedHashMap<>();

        json.put("idHotel", hotel.getIdHotel());
        json.put("nombre", hotel.getNombre());
        json.put("descripcion", hotel.getDescripcion());
        json.put("imagenPrincipal", hotel.getImagenPrincipal());
        json.put("provincia", hotel.getProvincia());
        json.put("canton", hotel.getCanton());
        json.put("direccion", hotel.getDireccion());
        json.put("telefono", hotel.getTelefono());
        json.put(
                "calificacionPromedio",
                hotel.getCalificacionPromedio()
        );
        json.put(
                "cuartosDisponibles",
                hotel.getCuartosDisponibles()
        );
        json.put("estado", hotel.getEstado());

        return json;
    }

    private String limpiar(String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return valor.trim();
    }
}
