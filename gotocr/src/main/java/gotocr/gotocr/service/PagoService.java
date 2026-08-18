package gotocr.gotocr.service;


import gotocr.gotocr.domain.Pago;
import gotocr.gotocr.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;

    public List<Pago> listarPagos() {
        return pagoRepository.listarPagos();
    }

    public Optional<Pago> buscarPorId(Integer idPago) {

        validarId(idPago);

        return pagoRepository.buscarPorId(idPago);
    }

    public List<Pago> buscarPorReserva(Integer idReserva) {

        validarId(idReserva);

        return pagoRepository.buscarPorReserva(idReserva);
    }

    public List<Pago> buscarPorEstado(String estadoPago) {

        validarTexto(
                estadoPago,
                "El estado del pago es obligatorio"
        );

        return pagoRepository.buscarPorEstado(
                estadoPago.trim()
        );
    }

    public List<Pago> buscarPorMetodoPago(String metodoPago) {

        validarTexto(
                metodoPago,
                "El método de pago es obligatorio"
        );

        return pagoRepository.buscarPorMetodoPago(
                metodoPago.trim()
        );
    }

    public void insertarPago(
            Integer idReserva,
            BigDecimal monto,
            String metodoPago,
            String estadoPago) {

        validarId(idReserva);

        validarMonto(monto);

        validarTexto(
                metodoPago,
                "El método de pago es obligatorio"
        );

        validarTexto(
                estadoPago,
                "El estado del pago es obligatorio"
        );

        pagoRepository.insertarPago(
                idReserva,
                monto,
                metodoPago.trim(),
                estadoPago.trim()
        );
    }

    public void actualizarPago(
            Integer idPago,
            Integer idReserva,
            BigDecimal monto,
            String metodoPago,
            String estadoPago) {

        validarId(idPago);
        validarId(idReserva);

        if (pagoRepository.buscarPorId(idPago).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el pago indicado"
            );
        }

        validarMonto(monto);

        validarTexto(
                metodoPago,
                "El método de pago es obligatorio"
        );

        validarTexto(
                estadoPago,
                "El estado del pago es obligatorio"
        );

        pagoRepository.actualizarPago(
                idPago,
                idReserva,
                monto,
                metodoPago.trim(),
                estadoPago.trim()
        );
    }

    public void eliminarPago(Integer idPago) {

        validarId(idPago);

        if (pagoRepository.buscarPorId(idPago).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe el pago indicado"
            );
        }

        pagoRepository.eliminarPago(idPago);
    }

    private void validarMonto(BigDecimal monto) {

        if (monto == null ||
                monto.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero"
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