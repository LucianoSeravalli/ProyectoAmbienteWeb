package gotocr.gotocr.controller;

import gotocr.gotocr.domain.CuartoHotel;
import gotocr.gotocr.domain.Hotel;
import gotocr.gotocr.domain.ImagenCuarto;
import gotocr.gotocr.domain.ResenaHotel;
import gotocr.gotocr.service.CuartoHotelService;
import gotocr.gotocr.service.HotelService;
import gotocr.gotocr.service.ResenaHotelService;
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
import org.springframework.web.multipart.MultipartFile;

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

    private Map<String, Object> cuartoAJson(CuartoHotel cuarto) {

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

        if (cuarto.getTipoCuarto() != null) {

            json.put(
                    "idTipoCuarto",
                    cuarto.getTipoCuarto().getIdTipoCuarto()
            );
            json.put(
                    "tipoCuarto",
                    cuarto.getTipoCuarto().getNombreTipo()
            );
        }
        Integer idImagen = null;
        if (cuarto.getImagenes() != null
                && !cuarto.getImagenes().isEmpty()) {
            ImagenCuarto primera
                    = cuarto.getImagenes().getFirst();
            idImagen
                    = primera.getIdImagen();
        }
        json.put(
                "idImagen",
                idImagen
        );
        json.put(
                "tieneImagen",
                idImagen != null
        );
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

    @GetMapping("/imagen/{idHotel}")
    @ResponseBody
    public ResponseEntity<byte[]> obtenerImagenHotel(
            @PathVariable Integer idHotel) {

        Hotel hotel
                = hotelService.buscarPorId(idHotel)
                        .orElse(null);

        if (hotel == null
                || hotel.getImagenPrincipal() == null
                || hotel.getImagenPrincipal().length == 0) {

            return ResponseEntity.notFound().build();
        }

        String tipo
                = hotel.getTipoImagenPrincipal();

        if (tipo == null || tipo.isBlank()) {
            tipo = "image/jpeg";
        }

        return ResponseEntity
                .ok()
                .header(
                        "Content-Type",
                        tipo
                )
                .body(
                        hotel.getImagenPrincipal()
                );
    }

    @PostMapping("/hoteles/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarHotel(
            @RequestParam(
                    name = "idHotel",
                    required = false
            ) Integer idHotel,
            @RequestParam String nombre,
            @RequestParam(
                    required = false
            ) String descripcion,
            @RequestParam(
                    name = "imagenPrincipal",
                    required = false
            ) MultipartFile imagenPrincipal,
            @RequestParam(
                    required = false
            ) String provincia,
            @RequestParam(
                    required = false
            ) String canton,
            @RequestParam(
                    required = false
            ) String direccion,
            @RequestParam(
                    required = false
            ) String telefono,
            @RequestParam(
                    required = false,
                    defaultValue = "0"
            ) BigDecimal calificacionPromedio,
            @RequestParam(
                    required = false,
                    defaultValue = "0"
            ) Integer cuartosDisponibles,
            @RequestParam String estado) {

        try {

            System.out.println(
                    "===== ENTRÓ A guardarHotel ====="
            );

            System.out.println(
                    "Nombre: " + nombre
            );

            System.out.println(
                    "Estado: " + estado
            );

            System.out.println(
                    "ID Hotel: " + idHotel
            );

            System.out.println(
                    "Imagen recibida: "
                    + (imagenPrincipal != null
                    && !imagenPrincipal.isEmpty())
            );

            if (idHotel == null) {

                hotelService.insertarHotel(
                        nombre,
                        descripcion,
                        imagenPrincipal,
                        provincia,
                        canton,
                        direccion,
                        telefono,
                        calificacionPromedio,
                        cuartosDisponibles,
                        estado
                );

                return ResponseEntity.ok(
                        Map.of(
                                "mensaje",
                                "Hotel registrado correctamente."
                        )
                );

            } else {

                hotelService.actualizarHotel(
                        idHotel,
                        nombre,
                        descripcion,
                        imagenPrincipal,
                        provincia,
                        canton,
                        direccion,
                        telefono,
                        calificacionPromedio,
                        cuartosDisponibles,
                        estado
                );

                return ResponseEntity.ok(
                        Map.of(
                                "mensaje",
                                "Hotel actualizado correctamente."
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage() != null
                                    ? e.getMessage()
                                    : "Error al guardar el hotel."
                            )
                    );
        }
    }

}
