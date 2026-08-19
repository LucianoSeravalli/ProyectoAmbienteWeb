package gotocr.gotocr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "IMAGENCUARTO")
public class ImagenCuarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idImagen")
    private Integer idImagen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCuartoHotel", nullable = false)
    private CuartoHotel cuartoHotel;

    @Lob
    @JsonIgnore
    @Column(
            name = "imagen",
            nullable = false,
            columnDefinition = "MEDIUMBLOB"
    )
    private byte[] imagen;

    @Column(
            name = "tipoImagen",
            nullable = false,
            length = 100
    )
    private String tipoImagen;
}
