package gotocr.gotocr.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "RESERVA")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idHotel", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCuartoHotel", nullable = false)
    private CuartoHotel cuartoHotel;

    @Column(name = "fechaEntrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fechaSalida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "cantidadPersonas", nullable = false)
    private Integer cantidadPersonas;

    @Column(name = "precioTotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "estadoReserva", nullable = false, length = 50)
    private String estadoReserva;

    @Column(name = "fechaReserva")
    private LocalDateTime fechaReserva;

    @OneToMany(mappedBy = "reserva")
    private List<Pago> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "reserva")

   private List<HistorialCliente> historial = new ArrayList<>();

/**
 * Valida que los datos básicos de la reserva sean coherentes.
 *
 * @return true si la reserva cumple las reglas básicas de negocio.
 */
public boolean validarReserva() {
    if (fechaEntrada == null || fechaSalida == null) {
        return false;
    }

    if (!fechaSalida.isAfter(fechaEntrada)) {
        return false;
    }

    if (cantidadPersonas == null || cantidadPersonas < 1) {
        return false;
    }

    if (precioTotal == null || precioTotal.compareTo(BigDecimal.ZERO) < 0) {
        return false;
    }

        return true;
    }
}