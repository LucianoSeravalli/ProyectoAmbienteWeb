package gotocr.gotocr.domain;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "HOTEL")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHotel")
    private Integer idHotel;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Lob
    @JsonIgnore
    @Column(name = "imagenPrincipal", columnDefinition = "MEDIUMBLOB")
    private byte[] imagenPrincipal;

    @Column(name = "tipoImagenPrincipal", length = 100)
    private String tipoImagenPrincipal;

    @Column(name = "provincia", length = 100)
    private String provincia;

    @Column(name = "canton", length = 100)
    private String canton;

    @Column(name = "direccion", length = 300)
    private String direccion;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "calificacionPromedio", precision = 3, scale = 2)
    private BigDecimal calificacionPromedio;

    @Column(name = "cuartosDisponibles")
    private Integer cuartosDisponibles;

    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @OneToMany(mappedBy = "hotel")
    private List<CuartoHotel> cuartos = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    private List<ResenaHotel> resenas = new ArrayList<>();
}
