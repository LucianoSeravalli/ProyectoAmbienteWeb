package gotocr.gotocr.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "CLIENTE")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCliente")
    private Integer idCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRol", nullable = false)
    private Rol rol;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true, length = 200)
    private String correo;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Lob
    @JsonIgnore
    @Column(name = "imagenPerfil", columnDefinition = "MEDIUMBLOB")
    private byte[] imagenPerfil;

    @Column(name = "tipoImagenPerfil", length = 100)
    private String tipoImagenPerfil;

    @Column(name = "tokenConfirmacion", length = 255)
    private String tokenConfirmacion;

    @Column(name = "correoVerificado")
    private Boolean correoVerificado = false;

    @Column(name = "fechaRegistro")
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "cliente")
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<ResenaHotel> resenas = new ArrayList<>();

    @OneToMany(mappedBy = "cliente")
    private List<HistorialCliente> historial = new ArrayList<>();
}
