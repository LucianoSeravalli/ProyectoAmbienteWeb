
package gotocr.gotocr.domain;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "CUARTOHOTEL")
public class CuartoHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCuartoHotel")
    private Integer idCuartoHotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idHotel", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTipoCuarto", nullable = false)
    private TipoCuarto tipoCuarto;

    @Column(name = "numeroCuarto", nullable = false)
    private Integer numeroCuarto;

    @Column(name = "cantidadPersonas", nullable = false)
    private Integer cantidadPersonas;

    @Column(name = "precioNoche", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @OneToMany(mappedBy = "cuartoHotel")
    private List<ImagenCuarto> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "cuartoHotel")
    private List<Reserva> reservas = new ArrayList<>();
}