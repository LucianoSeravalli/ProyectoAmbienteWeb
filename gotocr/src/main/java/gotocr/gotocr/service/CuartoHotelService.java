
package gotocr.gotocr.service;

import gotocr.gotocr.domain.CuartoHotel;
import gotocr.gotocr.repository.CuartoHotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuartoHotelService {

    private final CuartoHotelRepository cuartoHotelRepository;

    public List<CuartoHotel> listarCuartos() {
        return cuartoHotelRepository.listarCuartos();
    }

    public Optional<CuartoHotel> buscarPorId(Integer idCuartoHotel) {

        validarId(idCuartoHotel);

        return cuartoHotelRepository.buscarPorId(idCuartoHotel);
    }

    public List<CuartoHotel> buscarPorHotel(Integer idHotel) {

        validarId(idHotel);

        return cuartoHotelRepository.buscarPorHotel(idHotel);
    }

    public List<CuartoHotel> buscarPorTipo(Integer idTipoCuarto) {

        validarId(idTipoCuarto);

        return cuartoHotelRepository.buscarPorTipo(idTipoCuarto);
    }

    public List<CuartoHotel> buscarPorHotelYEstado(
            Integer idHotel,
            String estado) {

        validarId(idHotel);
        validarTexto(estado, "El estado es obligatorio");

        return cuartoHotelRepository.buscarPorHotelYEstado(
                idHotel,
                estado.trim()
        );
    }

    public List<CuartoHotel> buscarPorCapacidad(
            Integer cantidadPersonas) {

        validarNumeroPositivo(
                cantidadPersonas,
                "La cantidad de personas debe ser mayor que cero"
        );

        return cuartoHotelRepository.buscarPorCapacidad(
                cantidadPersonas
        );
    }

    public List<CuartoHotel> buscarPorRangoPrecio(
            BigDecimal precioMin,
            BigDecimal precioMax) {

        validarPrecio(precioMin);
        validarPrecio(precioMax);

        if (precioMin.compareTo(precioMax) > 0) {
            throw new IllegalArgumentException(
                    "El precio mínimo no puede ser mayor que el máximo"
            );
        }

        return cuartoHotelRepository.buscarPorRangoPrecio(
                precioMin,
                precioMax
        );
    }

    public void insertarCuartoHotel(
            Integer idHotel,
            Integer idTipoCuarto,
            Integer numeroCuarto,
            Integer cantidadPersonas,
            BigDecimal precioNoche,
            String estado) {

        validarId(idHotel);
        validarId(idTipoCuarto);

        validarNumeroPositivo(
                numeroCuarto,
                "El número de cuarto debe ser mayor que cero"
        );

        validarNumeroPositivo(
                cantidadPersonas,
                "La cantidad de personas debe ser mayor que cero"
        );

        validarPrecio(precioNoche);

        validarTexto(estado, "El estado es obligatorio");

        cuartoHotelRepository.insertarCuartoHotel(
                idHotel,
                idTipoCuarto,
                numeroCuarto,
                cantidadPersonas,
                precioNoche,
                estado.trim()
        );
    }

    public void actualizarCuartoHotel(
            Integer idCuartoHotel,
            Integer idHotel,
            Integer idTipoCuarto,
            Integer numeroCuarto,
            Integer cantidadPersonas,
            BigDecimal precioNoche,
            String estado) {

        validarId(idCuartoHotel);
        validarId(idHotel);
        validarId(idTipoCuarto);

        if (cuartoHotelRepository.buscarPorId(idCuartoHotel).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el cuarto indicado"
            );
        }

        validarNumeroPositivo(
                numeroCuarto,
                "El número de cuarto debe ser mayor que cero"
        );

        validarNumeroPositivo(
                cantidadPersonas,
                "La cantidad de personas debe ser mayor que cero"
        );

        validarPrecio(precioNoche);
        validarTexto(estado, "El estado es obligatorio");

        cuartoHotelRepository.actualizarCuartoHotel(
                idCuartoHotel,
                idHotel,
                idTipoCuarto,
                numeroCuarto,
                cantidadPersonas,
                precioNoche,
                estado.trim()
        );
    }

    public void eliminarCuartoHotel(Integer idCuartoHotel) {

        validarId(idCuartoHotel);

        if (cuartoHotelRepository.buscarPorId(idCuartoHotel).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el cuarto indicado"
            );
        }

        cuartoHotelRepository.eliminarCuartoHotel(idCuartoHotel);
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

    private void validarPrecio(BigDecimal precio) {

        if (precio == null ||
                precio.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "El precio no puede ser nulo ni negativo"
            );
        }
    }
}