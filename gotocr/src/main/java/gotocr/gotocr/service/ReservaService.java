package gotocr.gotocr.service;

import gotocr.gotocr.domain.Reserva;
import gotocr.gotocr.repository.PagoRepository;
import gotocr.gotocr.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final PagoService pagoService;
    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public List<Reserva> listarReservas() {
        return reservaRepository.listarReservas();
    }

    public Optional<Reserva> buscarPorId(Integer idReserva) {

        validarId(idReserva);

        return reservaRepository.buscarPorId(idReserva);
    }

    public List<Reserva> buscarPorCliente(Integer idCliente) {

        validarId(idCliente);

        return reservaRepository.buscarPorCliente(idCliente);
    }

    public List<Reserva> buscarPorHotel(Integer idHotel) {

        validarId(idHotel);

        return reservaRepository.buscarPorHotel(idHotel);
    }

    public List<Reserva> buscarPorCuarto(Integer idCuartoHotel) {

        validarId(idCuartoHotel);

        return reservaRepository.buscarPorCuarto(idCuartoHotel);
    }

    public List<Reserva> buscarPorEstado(String estado) {

        validarTexto(estado, "El estado es obligatorio");

        return reservaRepository.buscarPorEstado(estado.trim());
    }

    public List<Reserva> buscarPorRangoFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        validarFechas(fechaInicio, fechaFin);

        return reservaRepository.buscarPorRangoFechas(
                fechaInicio,
                fechaFin
        );
    }

    @Transactional
    public Integer insertarReserva(
            Integer idCliente,
            Integer idHotel,
            Integer idCuartoHotel,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            Integer cantidadPersonas,
            BigDecimal precioTotal,
            String estadoReserva,
            String metodoPago) {

        validarId(idCliente);
        validarId(idHotel);
        validarId(idCuartoHotel);

        validarFechas(
                fechaEntrada,
                fechaSalida
        );

        validarNumeroPositivo(
                cantidadPersonas,
                "La cantidad de personas debe ser mayor que cero"
        );

        validarPrecio(
                precioTotal,
                "El precio total no puede ser negativo"
        );

        validarTexto(
                estadoReserva,
                "El estado de la reserva es obligatorio"
        );

        validarTexto(
                metodoPago,
                "El método de pago es obligatorio"
        );

        Integer idReserva
                = reservaRepository.insertarReserva(
                        idCliente,
                        idHotel,
                        idCuartoHotel,
                        fechaEntrada,
                        fechaSalida,
                        cantidadPersonas,
                        precioTotal,
                        estadoReserva.trim()
                );

        if (idReserva == null || idReserva <= 0) {
            throw new IllegalStateException(
                    "No fue posible obtener el ID de la reserva"
            );
        }

        String estadoPago;

        if (metodoPago.equalsIgnoreCase("EFECTIVO")) {
            estadoPago = "PENDIENTE";
        } else {
            estadoPago = "PAGADO";
        }

        pagoService.insertarPago(
                idReserva,
                precioTotal,
                metodoPago.trim().toUpperCase(),
                estadoPago
        );

        return idReserva;
    }

    public void actualizarReserva(
            Integer idReserva,
            Integer idCliente,
            Integer idHotel,
            Integer idCuartoHotel,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            Integer cantidadPersonas,
            BigDecimal precioTotal,
            String estadoReserva) {

        validarId(idReserva);
        validarId(idCliente);
        validarId(idHotel);
        validarId(idCuartoHotel);

        if (reservaRepository.buscarPorId(idReserva).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe la reserva indicada"
            );
        }

        validarFechas(fechaEntrada, fechaSalida);

        validarNumeroPositivo(
                cantidadPersonas,
                "La cantidad de personas debe ser mayor que cero"
        );

        validarPrecio(
                precioTotal,
                "El precio total no puede ser negativo"
        );

        validarTexto(
                estadoReserva,
                "El estado de la reserva es obligatorio"
        );

        reservaRepository.actualizarReserva(
                idReserva,
                idCliente,
                idHotel,
                idCuartoHotel,
                fechaEntrada,
                fechaSalida,
                cantidadPersonas,
                precioTotal,
                estadoReserva.trim()
        );
    }

    public void eliminarReserva(Integer idReserva) {

        validarId(idReserva);

        if (reservaRepository.buscarPorId(idReserva).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe la reserva indicada"
            );
        }

        reservaRepository.eliminarReserva(idReserva);
    }

    private void validarFechas(
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        if (fechaEntrada == null) {
            throw new IllegalArgumentException(
                    "La fecha de entrada es obligatoria"
            );
        }

        if (fechaSalida == null) {
            throw new IllegalArgumentException(
                    "La fecha de salida es obligatoria"
            );
        }

        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException(
                    "La fecha de salida debe ser posterior a la fecha de entrada"
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

    private void validarNumeroPositivo(
            Integer numero,
            String mensaje) {

        if (numero == null || numero <= 0) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarPrecio(
            BigDecimal precio,
            String mensaje) {

        if (precio == null
                || precio.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(mensaje);
        }
    }

    @Transactional
    public Integer confirmarReserva(
            Integer idCliente,
            Integer idHotel,
            Integer idCuartoHotel,
            LocalDate fechaEntrada,
            LocalDate fechaSalida,
            Integer cantidadPersonas,
            BigDecimal precioTotal,
            String metodoPago) {

        validarId(idCliente);
        validarId(idHotel);
        validarId(idCuartoHotel);

        validarFechas(
                fechaEntrada,
                fechaSalida
        );

        validarNumeroPositivo(
                cantidadPersonas,
                "La cantidad de personas debe ser mayor que cero"
        );

        validarPrecio(
                precioTotal,
                "El precio total debe ser válido"
        );

        validarTexto(
                metodoPago,
                "El método de pago es obligatorio"
        );

        // 1. Crear reserva
        Integer idReserva
                = reservaRepository.insertarReserva(
                        idCliente,
                        idHotel,
                        idCuartoHotel,
                        fechaEntrada,
                        fechaSalida,
                        cantidadPersonas,
                        precioTotal,
                        "CONFIRMADA"
                );

        if (idReserva == null || idReserva <= 0) {
            throw new IllegalStateException(
                    "No fue posible crear la reserva"
            );
        }

        // 2. Determinar estado del pago
        String estadoPago;

        if (metodoPago.equalsIgnoreCase("EFECTIVO")) {
            estadoPago = "PENDIENTE";
        } else {
            estadoPago = "PAGADO";
        }

        // 3. Registrar pago
        pagoRepository.insertarPago(
                idReserva,
                precioTotal,
                metodoPago.trim().toUpperCase(),
                estadoPago
        );

        return idReserva;
    }

}
