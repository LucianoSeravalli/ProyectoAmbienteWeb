package gotocr.gotocr.service;


import gotocr.gotocr.domain.Hotel;
import gotocr.gotocr.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<Hotel> listarHoteles() {
        return hotelRepository.listarHoteles();
    }

    public Optional<Hotel> buscarPorId(Integer idHotel) {

        validarId(idHotel);

        return hotelRepository.buscarPorId(idHotel);
    }

    public List<Hotel> buscarPorNombre(String nombre) {

        validarTexto(nombre, "El nombre del hotel es obligatorio");

        return hotelRepository.buscarPorNombre(nombre.trim());
    }

    public List<Hotel> buscarPorProvincia(String provincia) {

        validarTexto(provincia, "La provincia es obligatoria");

        return hotelRepository.buscarPorProvincia(provincia.trim());
    }

    public List<Hotel> buscarPorCanton(String canton) {

        validarTexto(canton, "El cantón es obligatorio");

        return hotelRepository.buscarPorCanton(canton.trim());
    }

    public List<Hotel> buscarPorEstado(String estado) {

        validarTexto(estado, "El estado es obligatorio");

        return hotelRepository.buscarPorEstado(estado.trim());
    }

    public List<Hotel> buscarPorCalificacionMinima(
            BigDecimal calificacion) {

        if (calificacion == null ||
                calificacion.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "La calificación no puede ser negativa"
            );
        }

        return hotelRepository.buscarPorCalificacionMinima(
                calificacion
        );
    }

    public void insertarHotel(
            String nombre,
            String descripcion,
            String imagenPrincipal,
            String provincia,
            String canton,
            String direccion,
            String telefono,
            BigDecimal calificacionPromedio,
            Integer cuartosDisponibles,
            String estado) {

        validarTexto(nombre, "El nombre del hotel es obligatorio");
        validarTexto(descripcion, "La descripción es obligatoria");
        validarTexto(provincia, "La provincia es obligatoria");
        validarTexto(canton, "El cantón es obligatorio");
        validarTexto(direccion, "La dirección es obligatoria");
        validarTexto(telefono, "El teléfono es obligatorio");
        validarTexto(estado, "El estado es obligatorio");

        validarNumeroNoNegativo(
                calificacionPromedio,
                "La calificación no puede ser negativa"
        );

        validarNumeroPositivo(
                cuartosDisponibles,
                "La cantidad de cuartos debe ser mayor que cero"
        );

        hotelRepository.insertarHotel(
                nombre.trim(),
                descripcion.trim(),
                imagenPrincipal,
                provincia.trim(),
                canton.trim(),
                direccion.trim(),
                telefono.trim(),
                calificacionPromedio,
                cuartosDisponibles,
                estado.trim()
        );
    }

    public void actualizarHotel(
            Integer idHotel,
            String nombre,
            String descripcion,
            String imagenPrincipal,
            String provincia,
            String canton,
            String direccion,
            String telefono,
            BigDecimal calificacionPromedio,
            Integer cuartosDisponibles,
            String estado) {

        validarId(idHotel);

        if (hotelRepository.buscarPorId(idHotel).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el hotel indicado"
            );
        }

        validarTexto(nombre, "El nombre del hotel es obligatorio");
        validarTexto(descripcion, "La descripción es obligatoria");
        validarTexto(provincia, "La provincia es obligatoria");
        validarTexto(canton, "El cantón es obligatorio");
        validarTexto(direccion, "La dirección es obligatoria");
        validarTexto(telefono, "El teléfono es obligatorio");
        validarTexto(estado, "El estado es obligatorio");

        validarNumeroNoNegativo(
                calificacionPromedio,
                "La calificación no puede ser negativa"
        );

        validarNumeroPositivo(
                cuartosDisponibles,
                "La cantidad de cuartos debe ser mayor que cero"
        );

        hotelRepository.actualizarHotel(
                idHotel,
                nombre.trim(),
                descripcion.trim(),
                imagenPrincipal,
                provincia.trim(),
                canton.trim(),
                direccion.trim(),
                telefono.trim(),
                calificacionPromedio,
                cuartosDisponibles,
                estado.trim()
        );
    }

    public void eliminarHotel(Integer idHotel) {

        validarId(idHotel);

        if (hotelRepository.buscarPorId(idHotel).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el hotel indicado"
            );
        }

        hotelRepository.eliminarHotel(idHotel);
    }

    private void validarId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero"
            );
        }
    }

    private void validarTexto(String texto, String mensaje) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarNumeroPositivo(Integer numero, String mensaje) {
        if (numero == null || numero <= 0) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarNumeroNoNegativo(
            BigDecimal numero,
            String mensaje) {

        if (numero == null ||
                numero.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(mensaje);
        }
    }
}