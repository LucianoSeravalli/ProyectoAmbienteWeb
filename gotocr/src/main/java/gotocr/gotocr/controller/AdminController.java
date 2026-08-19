package gotocr.gotocr.controller;

import gotocr.gotocr.domain.CuartoHotel;
import gotocr.gotocr.domain.Hotel;
import gotocr.gotocr.domain.TipoCuarto;
import gotocr.gotocr.service.CuartoHotelService;
import gotocr.gotocr.service.HotelService;
import gotocr.gotocr.service.TipoCuartoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final HotelService hotelService;
    private final CuartoHotelService cuartoHotelService;
    private final TipoCuartoService tipoCuartoService;

    // =========================================================
    // DASHBOARD
    // =========================================================
    @GetMapping
    public String dashboard() {
        return "admin-dashboard";
    }

    // =========================================================
    // RESUMEN
    // =========================================================
    @GetMapping("/resumen")
    @ResponseBody
    public Map<String, Object> resumen() {

        List<Hotel> hoteles
                = hotelService.listarHoteles();

        long hotelesActivos
                = hoteles.stream()
                        .filter(h
                                -> h.getEstado() != null
                        && h.getEstado()
                                .equalsIgnoreCase("ACTIVO")
                        )
                        .count();

        int cuartosDisponibles
                = hoteles.stream()
                        .map(Hotel::getCuartosDisponibles)
                        .filter(valor -> valor != null)
                        .mapToInt(Integer::intValue)
                        .sum();

        Map<String, Object> json
                = new LinkedHashMap<>();

        json.put(
                "totalHoteles",
                hoteles.size()
        );

        json.put(
                "hotelesActivos",
                hotelesActivos
        );

        json.put(
                "cuartosDisponibles",
                cuartosDisponibles
        );

        return json;
    }

    // =========================================================
    // HOTELES
    // =========================================================
    @GetMapping("/hoteles")
    @ResponseBody
    public List<Map<String, Object>> listarHoteles() {

        return hotelService
                .listarHoteles()
                .stream()
                .map(this::hotelAJson)
                .toList();
    }

    @GetMapping("/hoteles/{idHotel}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> buscarHotel(
            @PathVariable Integer idHotel) {

        return hotelService
                .buscarPorId(idHotel)
                .map(hotel
                        -> ResponseEntity.ok(
                        hotelAJson(hotel)
                )
                )
                .orElseGet(()
                        -> ResponseEntity
                        .notFound()
                        .build()
                );
    }

    @PostMapping("/hoteles/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarHotel(
            @RequestParam(required = false) Integer idHotel,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(
                    name = "imagenPrincipal",
                    required = false
            ) MultipartFile imagenPrincipal,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String canton,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String telefono,
            @RequestParam String estado) {

        try {

            if (idHotel == null) {

                /*
             * Hotel nuevo:
             *
             * La calificación empieza en 0.
             * Los cuartos empiezan en 0.
                 */
                hotelService.insertarHotel(
                        nombre,
                        descripcion,
                        imagenPrincipal,
                        provincia,
                        canton,
                        direccion,
                        telefono,
                        BigDecimal.ZERO,
                        0,
                        estado
                );

            } else {

                /*
             * Al editar NO debemos resetear
             * calificación ni cantidad de cuartos.
                 */
                Hotel hotelActual
                        = hotelService
                                .buscarPorId(idHotel)
                                .orElseThrow(()
                                        -> new IllegalArgumentException(
                                        "El hotel no existe."
                                )
                                );

                BigDecimal calificacionActual
                        = hotelActual.getCalificacionPromedio() != null
                        ? hotelActual.getCalificacionPromedio()
                        : BigDecimal.ZERO;

                Integer cuartosActuales
                        = hotelActual.getCuartosDisponibles() != null
                        ? hotelActual.getCuartosDisponibles()
                        : 0;

                hotelService.actualizarHotel(
                        idHotel,
                        nombre,
                        descripcion,
                        imagenPrincipal,
                        provincia,
                        canton,
                        direccion,
                        telefono,
                        calificacionActual,
                        cuartosActuales,
                        estado
                );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            idHotel == null
                                    ? "Hotel creado correctamente."
                                    : "Hotel actualizado correctamente."
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
                                    : "No fue posible guardar el hotel."
                            )
                    );
        }
    }

    @PostMapping("/hoteles/eliminar/{idHotel}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarHotel(
            @PathVariable Integer idHotel) {

        try {

            hotelService.eliminarHotel(
                    idHotel
            );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Hotel eliminado correctamente."
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "No fue posible eliminar el hotel."
                            )
                    );
        }
    }

    // =========================================================
    // TIPOS DE CUARTO
    // =========================================================
    @GetMapping("/tipos-cuarto")
    @ResponseBody
    public List<Map<String, Object>> listarTiposCuarto() {

        return tipoCuartoService
                .listarTipos()
                .stream()
                .map(this::tipoCuartoAJson)
                .toList();
    }

    // =========================================================
    // CUARTOS
    // =========================================================
    @GetMapping("/hoteles/{idHotel}/cuartos")
    @ResponseBody
    public List<Map<String, Object>> listarCuartosHotel(
            @PathVariable Integer idHotel) {

        return cuartoHotelService
                .buscarPorHotel(idHotel)
                .stream()
                .map(this::cuartoAJson)
                .toList();
    }

    @PostMapping("/cuartos/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarCuarto(
            @RequestParam(
                    required = false
            ) Integer idCuartoHotel,
            @RequestParam Integer idHotel,
            @RequestParam Integer idTipoCuarto,
            @RequestParam Integer numeroCuarto,
            @RequestParam Integer cantidadPersonas,
            @RequestParam BigDecimal precioNoche,
            @RequestParam String estado) {

        try {

            if (idCuartoHotel == null) {

                cuartoHotelService.insertarCuartoHotel(
                        idHotel,
                        idTipoCuarto,
                        numeroCuarto,
                        cantidadPersonas,
                        precioNoche,
                        estado
                );

            } else {

                cuartoHotelService.actualizarCuartoHotel(
                        idCuartoHotel,
                        idHotel,
                        idTipoCuarto,
                        numeroCuarto,
                        cantidadPersonas,
                        precioNoche,
                        estado
                );
            }

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            idCuartoHotel == null
                                    ? "Cuarto registrado correctamente."
                                    : "Cuarto actualizado correctamente."
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
                                    : "No fue posible guardar el cuarto."
                            )
                    );
        }
    }

    @PostMapping("/cuartos/eliminar/{idCuartoHotel}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarCuarto(
            @PathVariable Integer idCuartoHotel) {

        try {

            cuartoHotelService.eliminarCuartoHotel(
                    idCuartoHotel
            );

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje",
                            "Cuarto eliminado correctamente."
                    )
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "No fue posible eliminar el cuarto."
                            )
                    );
        }
    }

    // =========================================================
    // JSON
    // =========================================================
    private Map<String, Object> hotelAJson(
            Hotel hotel) {

        Map<String, Object> json
                = new LinkedHashMap<>();

        json.put(
                "idHotel",
                hotel.getIdHotel()
        );

        json.put(
                "nombre",
                hotel.getNombre()
        );

        json.put(
                "descripcion",
                hotel.getDescripcion()
        );

        json.put(
                "tieneImagen",
                hotel.getImagenPrincipal() != null
                && hotel.getImagenPrincipal().length > 0
        );

        json.put(
                "provincia",
                hotel.getProvincia()
        );

        json.put(
                "canton",
                hotel.getCanton()
        );

        json.put(
                "direccion",
                hotel.getDireccion()
        );

        json.put(
                "telefono",
                hotel.getTelefono()
        );

        json.put(
                "calificacionPromedio",
                hotel.getCalificacionPromedio()
        );

        json.put(
                "cuartosDisponibles",
                hotel.getCuartosDisponibles()
        );

        json.put(
                "estado",
                hotel.getEstado()
        );

        return json;
    }

    private Map<String, Object> tipoCuartoAJson(
            TipoCuarto tipo) {

        Map<String, Object> json
                = new LinkedHashMap<>();

        json.put(
                "idTipoCuarto",
                tipo.getIdTipoCuarto()
        );

        json.put(
                "nombreTipo",
                tipo.getNombreTipo()
        );

        json.put(
                "descripcion",
                tipo.getDescripcion()
        );

        return json;
    }

    private Map<String, Object> cuartoAJson(
            CuartoHotel cuarto) {

        Map<String, Object> json
                = new LinkedHashMap<>();

        json.put(
                "idCuartoHotel",
                cuarto.getIdCuartoHotel()
        );

        json.put(
                "numeroCuarto",
                cuarto.getNumeroCuarto()
        );

        json.put(
                "cantidadPersonas",
                cuarto.getCantidadPersonas()
        );

        json.put(
                "precioNoche",
                cuarto.getPrecioNoche()
        );

        json.put(
                "estado",
                cuarto.getEstado()
        );

        if (cuarto.getHotel() != null) {

            json.put(
                    "idHotel",
                    cuarto.getHotel().getIdHotel()
            );

            json.put(
                    "hotel",
                    cuarto.getHotel().getNombre()
            );
        }

        if (cuarto.getTipoCuarto() != null) {

            json.put(
                    "idTipoCuarto",
                    cuarto
                            .getTipoCuarto()
                            .getIdTipoCuarto()
            );

            json.put(
                    "tipoCuarto",
                    cuarto
                            .getTipoCuarto()
                            .getNombreTipo()
            );
        }

        return json;
    }
}
