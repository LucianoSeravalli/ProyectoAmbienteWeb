package gotocr.gotocr.service;

import gotocr.gotocr.domain.CuartoHotel;
import gotocr.gotocr.repository.CuartoHotelRepository;
import gotocr.gotocr.repository.HotelRepository;
import gotocr.gotocr.repository.TipoCuartoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuartoHotelService {

    private final CuartoHotelRepository cuartoHotelRepository;

    private final HotelRepository hotelRepository;

    private final TipoCuartoRepository tipoCuartoRepository;

    public List<CuartoHotel> buscarPorHotel(
            Integer idHotel) {

        validarId(
                idHotel,
                "El hotel es obligatorio."
        );

        return cuartoHotelRepository
                .buscarPorHotel(idHotel);
    }

    public Optional<CuartoHotel> buscarPorId(
            Integer idCuartoHotel) {

        return cuartoHotelRepository
                .findById(idCuartoHotel);
    }

    @Transactional
    public void insertarCuartoHotel(
            Integer idHotel,
            Integer idTipoCuarto,
            Integer numeroCuarto,
            Integer cantidadPersonas,
            BigDecimal precioNoche,
            String estado) {

        validarDatos(
                idHotel,
                idTipoCuarto,
                numeroCuarto,
                cantidadPersonas,
                precioNoche,
                estado
        );

        validarRelaciones(
                idHotel,
                idTipoCuarto
        );

        cuartoHotelRepository
                .insertarCuartoHotel(
                        idHotel,
                        idTipoCuarto,
                        numeroCuarto,
                        cantidadPersonas,
                        precioNoche,
                        estado.trim()
                );
        hotelRepository
                .recalcularCuartosHotel(
                        idHotel
                );
    }

    @Transactional
    public void actualizarCuartoHotel(
            Integer idCuartoHotel,
            Integer idHotel,
            Integer idTipoCuarto,
            Integer numeroCuarto,
            Integer cantidadPersonas,
            BigDecimal precioNoche,
            String estado) {

        validarId(
                idCuartoHotel,
                "El cuarto es obligatorio."
        );

        validarDatos(
                idHotel,
                idTipoCuarto,
                numeroCuarto,
                cantidadPersonas,
                precioNoche,
                estado
        );

        if (!cuartoHotelRepository
                .existsById(idCuartoHotel)) {

            throw new IllegalArgumentException(
                    "El cuarto indicado no existe."
            );
        }

        validarRelaciones(
                idHotel,
                idTipoCuarto
        );

        cuartoHotelRepository
                .actualizarCuartoHotel(
                        idCuartoHotel,
                        idHotel,
                        idTipoCuarto,
                        numeroCuarto,
                        cantidadPersonas,
                        precioNoche,
                        estado.trim()
                );
    }

    @Transactional
    public void eliminarCuartoHotel(
            Integer idCuartoHotel) {

        validarId(
                idCuartoHotel,
                "El cuarto es obligatorio."
        );

        CuartoHotel cuarto
                = cuartoHotelRepository
                        .findById(idCuartoHotel)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                "El cuarto indicado no existe."
                        )
                        );

        Integer idHotel
                = cuarto.getHotel()
                        .getIdHotel();

        cuartoHotelRepository
                .eliminarCuartoHotel(
                        idCuartoHotel
                );

        hotelRepository
                .recalcularCuartosHotel(
                        idHotel
                );
    }

    private void validarRelaciones(
            Integer idHotel,
            Integer idTipoCuarto) {

        if (!hotelRepository
                .existsById(idHotel)) {

            throw new IllegalArgumentException(
                    "El hotel indicado no existe."
            );
        }

        if (!tipoCuartoRepository
                .existsById(idTipoCuarto)) {

            throw new IllegalArgumentException(
                    "El tipo de cuarto indicado no existe."
            );
        }
    }

    private void validarDatos(
            Integer idHotel,
            Integer idTipoCuarto,
            Integer numeroCuarto,
            Integer cantidadPersonas,
            BigDecimal precioNoche,
            String estado) {

        validarId(
                idHotel,
                "El hotel es obligatorio."
        );

        validarId(
                idTipoCuarto,
                "El tipo de cuarto es obligatorio."
        );

        if (numeroCuarto == null
                || numeroCuarto <= 0) {

            throw new IllegalArgumentException(
                    "El número de cuarto debe ser mayor a 0."
            );
        }

        if (cantidadPersonas == null
                || cantidadPersonas <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad de personas debe ser mayor a 0."
            );
        }

        if (precioNoche == null
                || precioNoche.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new IllegalArgumentException(
                    "El precio por noche debe ser mayor a 0."
            );
        }

        if (estado == null
                || estado.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El estado es obligatorio."
            );
        }
    }

    private void validarId(
            Integer id,
            String mensaje) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    mensaje
            );
        }
    }
}
