package gotocr.gotocr.service;

import gotocr.gotocr.domain.Hotel;
import gotocr.gotocr.repository.HotelRepository;
import gotocr.gotocr.service.util.ImagenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

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
        validarTexto(provincia, "La provincia es obligatoria para realizar la búsqueda");
        return hotelRepository.buscarPorProvincia(provincia.trim());
    }

    public List<Hotel> buscarPorCanton(String canton) {
        validarTexto(canton, "El cantón es obligatorio para realizar la búsqueda");
        return hotelRepository.buscarPorCanton(canton.trim());
    }

    public List<Hotel> buscarPorEstado(String estado) {
        validarTexto(estado, "El estado es obligatorio");
        return hotelRepository.buscarPorEstado(estado.trim());
    }

    public List<Hotel> buscarPorCalificacionMinima(
            BigDecimal calificacion) {

        if (calificacion == null
                || calificacion.compareTo(BigDecimal.ZERO) < 0
                || calificacion.compareTo(new BigDecimal("5.00")) > 0) {

            throw new IllegalArgumentException(
                    "La calificación debe estar entre 0 y 5"
            );
        }

        return hotelRepository.buscarPorCalificacionMinima(
                calificacion
        );
    }

    @Transactional
    public void insertarHotel(
            String nombre,
            String descripcion,
            MultipartFile archivo,
            String provincia,
            String canton,
            String direccion,
            String telefono,
            BigDecimal calificacionPromedio,
            Integer cuartosDisponibles,
            String estado) {

        validarTexto(
                nombre,
                "El nombre del hotel es obligatorio"
        );

        validarTexto(
                estado,
                "El estado es obligatorio"
        );

        if (calificacionPromedio == null) {
            calificacionPromedio
                    = BigDecimal.ZERO;
        }

        if (cuartosDisponibles == null) {
            cuartosDisponibles = 0;
        }

        byte[] imagen = null;
        String tipoImagen = null;
        if (archivo != null && !archivo.isEmpty()) {

            ImagenUtil.validar(archivo);
            imagen = ImagenUtil.obtenerBytes(archivo);
            tipoImagen = archivo.getContentType();
        }

        hotelRepository.insertarHotel(
                nombre.trim(),
                normalizar(descripcion),
                imagen,
                tipoImagen,
                normalizar(provincia),
                normalizar(canton),
                normalizar(direccion),
                normalizar(telefono),
                calificacionPromedio,
                cuartosDisponibles,
                estado.trim()
        );
    }

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    @Transactional
    public void actualizarHotel(
            Integer idHotel,
            String nombre,
            String descripcion,
            MultipartFile imagenPrincipal,
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

        validarDatosHotel(
                nombre,
                calificacionPromedio,
                cuartosDisponibles,
                estado
        );

        byte[] imagenBytes = null;
        String tipoImagen = null;

        if (imagenPrincipal != null
                && !imagenPrincipal.isEmpty()) {

            ImagenUtil.validar(imagenPrincipal);

            imagenBytes
                    = ImagenUtil.obtenerBytes(
                            imagenPrincipal
                    );

            tipoImagen
                    = imagenPrincipal.getContentType();
        }

        hotelRepository.actualizarHotel(
                idHotel,
                nombre.trim(),
                limpiarOpcional(descripcion),
                imagenBytes,
                tipoImagen,
                limpiarOpcional(provincia),
                limpiarOpcional(canton),
                limpiarOpcional(direccion),
                limpiarOpcional(telefono),
                calificacionPromedio,
                cuartosDisponibles,
                estado.trim()
        );
    }

    @Transactional
    public void eliminarHotel(Integer idHotel) {

        validarId(idHotel);

        if (hotelRepository.buscarPorId(idHotel).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el hotel indicado"
            );
        }

        hotelRepository.eliminarHotel(idHotel);
    }

    private void validarDatosHotel(
            String nombre,
            BigDecimal calificacionPromedio,
            Integer cuartosDisponibles,
            String estado) {

        validarTexto(
                nombre,
                "El nombre del hotel es obligatorio"
        );

        validarTexto(
                estado,
                "El estado del hotel es obligatorio"
        );

        if (calificacionPromedio == null) {
            throw new IllegalArgumentException(
                    "La calificación promedio no puede ser nula"
            );
        }

        if (calificacionPromedio.compareTo(BigDecimal.ZERO) < 0
                || calificacionPromedio.compareTo(
                        new BigDecimal("5.00")
                ) > 0) {

            throw new IllegalArgumentException(
                    "La calificación debe estar entre 0 y 5"
            );
        }

        if (cuartosDisponibles == null
                || cuartosDisponibles < 0) {

            throw new IllegalArgumentException(
                    "La cantidad de cuartos disponibles no puede ser negativa"
            );
        }
    }

    private void validarId(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser mayor que cero"
            );
        }
    }

    private void validarTexto(
            String texto,
            String mensaje) {

        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private String limpiarOpcional(String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return valor.trim();
    }
}
