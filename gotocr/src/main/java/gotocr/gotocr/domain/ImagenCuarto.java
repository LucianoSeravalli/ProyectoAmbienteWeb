
package gotocr.gotocr.domain;

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

    @Column(name = "urlImagen", nullable = false, length = 500)
    private String urlImagen;
}