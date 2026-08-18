
package gotocr.gotocr.domain;

import lombok.Data;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "TIPOCUARTO")
public class TipoCuarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipoCuarto")
    private Integer idTipoCuarto;

    @Column(name = "nombreTipo", nullable = false, length = 100)
    private String nombreTipo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @OneToMany(mappedBy = "tipoCuarto")
    private List<CuartoHotel> cuartos = new ArrayList<>();
}