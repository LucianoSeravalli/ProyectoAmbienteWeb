package gotocr.gotocr.service;

import gotocr.gotocr.domain.ResenaHotel;
import gotocr.gotocr.repository.HotelRepository;
import gotocr.gotocr.repository.ResenaHotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResenaHotelService {

    private final ResenaHotelRepository resenaHotelRepository;

    private final HotelRepository hotelRepository;

    public List<ResenaHotel> buscarPorHotel(
            Integer idHotel) {

        return resenaHotelRepository
                .buscarPorHotel(idHotel);
    }

    public Optional<ResenaHotel> buscarPorId(
            Integer idResena) {

        return resenaHotelRepository
                .findById(idResena);
    }

    public Optional<ResenaHotel> buscarPorClienteYHotel(
            Integer idCliente,
            Integer idHotel) {

        return resenaHotelRepository
                .buscarPorClienteYHotel(
                        idCliente,
                        idHotel
                );
    }

    @Transactional
    public void guardarResena(
            Integer idCliente,
            Integer idHotel,
            Integer calificacion,
            String comentario) {

        validarDatos(
                idCliente,
                idHotel,
                calificacion
        );

        Optional<ResenaHotel> existente
                = resenaHotelRepository
                        .buscarPorClienteYHotel(
                                idCliente,
                                idHotel
                        );


        /*
         * Para simplificar:
         * un cliente tiene una reseña por hotel.
         *
         * Si ya existe, la actualizamos.
         */
        if (existente.isPresent()) {

            resenaHotelRepository
                    .actualizarResena(
                            existente.get()
                                    .getIdResena(),
                            calificacion,
                            limpiar(comentario)
                    );

        } else {

            resenaHotelRepository
                    .insertarResena(
                            idCliente,
                            idHotel,
                            calificacion,
                            limpiar(comentario)
                    );
        }

        hotelRepository
                .recalcularCalificacionHotel(
                        idHotel
                );
    }

    @Transactional
    public void eliminarResena(
            Integer idResena,
            Integer idCliente) {

        ResenaHotel resena
                = resenaHotelRepository
                        .findById(idResena)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                "La reseña no existe."
                        )
                        );

        if (resena.getCliente() == null
                || !resena.getCliente()
                        .getIdCliente()
                        .equals(idCliente)) {

            throw new IllegalArgumentException(
                    "No puede eliminar esta reseña."
            );
        }

        Integer idHotel
                = resena.getHotel()
                        .getIdHotel();

        resenaHotelRepository
                .eliminarResena(
                        idResena
                );

        hotelRepository
                .recalcularCalificacionHotel(
                        idHotel
                );
    }

    private void validarDatos(
            Integer idCliente,
            Integer idHotel,
            Integer calificacion) {

        if (idCliente == null
                || idCliente <= 0) {

            throw new IllegalArgumentException(
                    "Cliente inválido."
            );
        }

        if (idHotel == null
                || idHotel <= 0) {

            throw new IllegalArgumentException(
                    "Hotel inválido."
            );
        }

        if (calificacion == null
                || calificacion < 1
                || calificacion > 5) {

            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 5."
            );
        }
    }

    private String limpiar(
            String valor) {

        if (valor == null
                || valor.trim().isEmpty()) {

            return null;
        }

        return valor.trim();
    }
    
    @Transactional
public void insertarResenaHotel(
        Integer idCliente,
        Integer idHotel,
        Integer calificacion,
        String comentario) {

    if (idCliente == null || idCliente <= 0) {
        throw new IllegalArgumentException(
                "El cliente es obligatorio."
        );
    }

    if (idHotel == null || idHotel <= 0) {
        throw new IllegalArgumentException(
                "El hotel es obligatorio."
        );
    }

    if (calificacion == null
            || calificacion < 1
            || calificacion > 5) {

        throw new IllegalArgumentException(
                "La calificación debe estar entre 1 y 5."
        );
    }

    String comentarioLimpio =
            comentario == null
                    || comentario.trim().isEmpty()
                    ? null
                    : comentario.trim();

    resenaHotelRepository.insertarResenaHotel(
            idCliente,
            idHotel,
            calificacion,
            comentarioLimpio
    );

    hotelRepository.recalcularCalificacionHotel(
            idHotel
    );
}
}
