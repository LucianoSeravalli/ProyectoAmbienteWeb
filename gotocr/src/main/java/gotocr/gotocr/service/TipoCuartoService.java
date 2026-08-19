
package gotocr.gotocr.service;

import gotocr.gotocr.domain.TipoCuarto;
import gotocr.gotocr.repository.TipoCuartoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipoCuartoService {

    private final TipoCuartoRepository tipoCuartoRepository;

    public List<TipoCuarto> listarTiposCuarto() {
        return tipoCuartoRepository.listarTiposCuarto();
    }

    public Optional<TipoCuarto> buscarPorId(Integer idTipoCuarto) {

        validarId(idTipoCuarto);

        return tipoCuartoRepository.buscarPorId(idTipoCuarto);
    }

    public List<TipoCuarto> buscarPorNombre(String nombreTipo) {

        validarTexto(
                nombreTipo,
                "El nombre del tipo de cuarto es obligatorio"
        );

        return tipoCuartoRepository.buscarPorNombre(
                nombreTipo.trim()
        );
    }

    public void insertarTipoCuarto(
            String nombreTipo,
            String descripcion) {

        validarTexto(
                nombreTipo,
                "El nombre del tipo de cuarto es obligatorio"
        );

        validarTexto(
                descripcion,
                "La descripción es obligatoria"
        );

        tipoCuartoRepository.insertarTipoCuarto(
                nombreTipo.trim(),
                descripcion.trim()
        );
    }

    public void actualizarTipoCuarto(
            Integer idTipoCuarto,
            String nombreTipo,
            String descripcion) {

        validarId(idTipoCuarto);

        if (tipoCuartoRepository.buscarPorId(idTipoCuarto).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el tipo de cuarto indicado"
            );
        }

        validarTexto(
                nombreTipo,
                "El nombre del tipo de cuarto es obligatorio"
        );

        validarTexto(
                descripcion,
                "La descripción es obligatoria"
        );

        tipoCuartoRepository.actualizarTipoCuarto(
                idTipoCuarto,
                nombreTipo.trim(),
                descripcion.trim()
        );
    }

    public void eliminarTipoCuarto(Integer idTipoCuarto) {

        validarId(idTipoCuarto);

        if (tipoCuartoRepository.buscarPorId(idTipoCuarto).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el tipo de cuarto indicado"
            );
        }

        tipoCuartoRepository.eliminarTipoCuarto(idTipoCuarto);
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
    


    public List<TipoCuarto> listarTipos() {

        return tipoCuartoRepository
                .findAll();
    }


    
}