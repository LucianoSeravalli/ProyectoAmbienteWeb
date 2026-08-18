package gotocr.gotocr.service;



import gotocr.gotocr.domain.HistorialCliente;
import gotocr.gotocr.repository.HistorialClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialClienteService {

    private final HistorialClienteRepository historialClienteRepository;

    public List<HistorialCliente> listarHistorial() {
        return historialClienteRepository.listarHistorial();
    }

    public List<HistorialCliente> buscarPorCliente(
            Integer idCliente) {

        validarId(idCliente);

        return historialClienteRepository.buscarPorCliente(
                idCliente
        );
    }

    public List<HistorialCliente> buscarPorReserva(
            Integer idReserva) {

        validarId(idReserva);

        return historialClienteRepository.buscarPorReserva(
                idReserva
        );
    }

    public List<HistorialCliente> buscarPorAccion(
            String accion) {

        validarTexto(accion, "La acción es obligatoria");

        return historialClienteRepository.buscarPorAccion(
                accion.trim()
        );
    }

    public void insertarHistorialCliente(
            Integer idCliente,
            Integer idReserva,
            String accion) {

        validarId(idCliente);
        validarId(idReserva);

        validarTexto(
                accion,
                "La acción es obligatoria"
        );

        historialClienteRepository.insertarHistorialCliente(
                idCliente,
                idReserva,
                accion.trim()
        );
    }

    public void actualizarHistorialCliente(
            Integer idHistorialCliente,
            Integer idCliente,
            Integer idReserva,
            String accion) {

        validarId(idHistorialCliente);
        validarId(idCliente);
        validarId(idReserva);

        validarTexto(
                accion,
                "La acción es obligatoria"
        );

        historialClienteRepository.actualizarHistorialCliente(
                idHistorialCliente,
                idCliente,
                idReserva,
                accion.trim()
        );
    }

    public void eliminarHistorialCliente(
            Integer idHistorialCliente) {

        validarId(idHistorialCliente);

        if (historialClienteRepository
                .findById(idHistorialCliente)
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "No existe el registro de historial indicado"
            );
        }

        historialClienteRepository.eliminarHistorialCliente(
                idHistorialCliente
        );
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