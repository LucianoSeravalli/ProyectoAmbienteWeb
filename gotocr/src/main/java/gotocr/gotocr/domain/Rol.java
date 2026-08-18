package gotocr.gotocr.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "ROL")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRol")
    private Integer idRol;

    @Column(name = "nombreRol", nullable = false, length = 100)
    private String nombreRol;

    @OneToMany(mappedBy = "rol")
    private List<Cliente> clientes = new ArrayList<>();
}
