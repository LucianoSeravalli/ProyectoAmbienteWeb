/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gotocr.gotocr.service;

import gotocr.gotocr.domain.ImagenCuarto;
import gotocr.gotocr.repository.ImagenCuartoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImagenCuartoService {

    private final ImagenCuartoRepository imagenCuartoRepository;

    public List<ImagenCuarto> listarImagenes() {
        return imagenCuartoRepository.findAll();
    }

    public List<ImagenCuarto> buscarPorCuarto(
            Integer idCuartoHotel) {

        validarId(idCuartoHotel);

        return imagenCuartoRepository.buscarPorCuarto(
                idCuartoHotel
        );
    }

    public void insertarImagenCuarto(
            Integer idCuartoHotel,
            String urlImagen) {

        validarId(idCuartoHotel);
        validarTexto(
                urlImagen,
                "La URL de la imagen es obligatoria"
        );

        imagenCuartoRepository.insertarImagenCuarto(
                idCuartoHotel,
                urlImagen.trim()
        );
    }

    public void actualizarImagenCuarto(
            Integer idImagen,
            Integer idCuartoHotel,
            String urlImagen) {

        validarId(idImagen);
        validarId(idCuartoHotel);

        validarTexto(
                urlImagen,
                "La URL de la imagen es obligatoria"
        );

        if (imagenCuartoRepository.findById(idImagen).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe la imagen indicada"
            );
        }

        imagenCuartoRepository.actualizarImagenCuarto(
                idImagen,
                idCuartoHotel,
                urlImagen.trim()
        );
    }

    public void eliminarImagenCuarto(Integer idImagen) {

        validarId(idImagen);

        if (imagenCuartoRepository.findById(idImagen).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe la imagen indicada"
            );
        }

        imagenCuartoRepository.eliminarImagenCuarto(idImagen);
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

    public Optional<ImagenCuarto> buscarPorId(Integer idImagen) {
        return imagenCuartoRepository.findById(idImagen);
    }
}
