/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gotocr.gotocr.service;

import gotocr.gotocr.domain.ResenaHotel;
import gotocr.gotocr.repository.ResenaHotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResenaHotelService {

    private final ResenaHotelRepository resenaHotelRepository;

    public List<ResenaHotel> listarResenas() {
        return resenaHotelRepository.listarResenas();
    }

    public Optional<ResenaHotel> buscarPorId(Integer idResena) {

        validarId(idResena);

        return resenaHotelRepository.buscarPorId(idResena);
    }

    public List<ResenaHotel> buscarPorHotel(Integer idHotel) {

        validarId(idHotel);

        return resenaHotelRepository.buscarPorHotel(idHotel);
    }

    public List<ResenaHotel> buscarPorCliente(Integer idCliente) {

        validarId(idCliente);

        return resenaHotelRepository.buscarPorCliente(idCliente);
    }

    public List<ResenaHotel> buscarPorCalificacion(
            Integer calificacion) {

        validarCalificacion(calificacion);

        return resenaHotelRepository.buscarPorCalificacion(
                calificacion
        );
    }

    public void insertarResenaHotel(
            Integer idCliente,
            Integer idHotel,
            Integer calificacion,
            String comentario) {

        validarId(idCliente);
        validarId(idHotel);
        validarCalificacion(calificacion);

        validarTexto(
                comentario,
                "El comentario es obligatorio"
        );

        resenaHotelRepository.insertarResenaHotel(
                idCliente,
                idHotel,
                calificacion,
                comentario.trim()
        );
    }

    public void actualizarResenaHotel(
            Integer idResena,
            Integer idCliente,
            Integer idHotel,
            Integer calificacion,
            String comentario) {

        validarId(idResena);
        validarId(idCliente);
        validarId(idHotel);

        if (resenaHotelRepository.buscarPorId(idResena).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe la reseña indicada"
            );
        }

        validarCalificacion(calificacion);

        validarTexto(
                comentario,
                "El comentario es obligatorio"
        );

        resenaHotelRepository.actualizarResenaHotel(
                idResena,
                idCliente,
                idHotel,
                calificacion,
                comentario.trim()
        );
    }

    public void eliminarResenaHotel(Integer idResena) {

        validarId(idResena);

        if (resenaHotelRepository.buscarPorId(idResena).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe la reseña indicada"
            );
        }

        resenaHotelRepository.eliminarResenaHotel(idResena);
    }

    private void validarCalificacion(Integer calificacion) {

        if (calificacion == null ||
                calificacion < 1 ||
                calificacion > 5) {

            throw new IllegalArgumentException(
                    "La calificación debe estar entre 1 y 5"
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

    private void validarTexto(String texto, String mensaje) {

        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}