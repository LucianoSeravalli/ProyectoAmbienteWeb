
package gotocr.gotocr.repository;



import gotocr.gotocr.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    @Procedure(procedureName = "sp_insert_hotel")
    void insertarHotel(
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_imagenPrincipal") String imagenPrincipal,
            @Param("p_provincia") String provincia,
            @Param("p_canton") String canton,
            @Param("p_direccion") String direccion,
            @Param("p_telefono") String telefono,
            @Param("p_calificacionPromedio") BigDecimal calificacionPromedio,
            @Param("p_cuartosDisponibles") Integer cuartosDisponibles,
            @Param("p_estado") String estado
    );

    @Procedure(procedureName = "sp_update_hotel")
    void actualizarHotel(
            @Param("p_idHotel") Integer idHotel,
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_imagenPrincipal") String imagenPrincipal,
            @Param("p_provincia") String provincia,
            @Param("p_canton") String canton,
            @Param("p_direccion") String direccion,
            @Param("p_telefono") String telefono,
            @Param("p_calificacionPromedio") BigDecimal calificacionPromedio,
            @Param("p_cuartosDisponibles") Integer cuartosDisponibles,
            @Param("p_estado") String estado
    );

    @Procedure(procedureName = "sp_delete_hotel")
    void eliminarHotel(
            @Param("p_idHotel") Integer idHotel
    );
}