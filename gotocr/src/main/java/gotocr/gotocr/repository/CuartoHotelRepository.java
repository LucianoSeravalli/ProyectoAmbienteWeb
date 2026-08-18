
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.CuartoHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface CuartoHotelRepository extends JpaRepository<CuartoHotel, Integer> {

    @Procedure(procedureName = "sp_insert_cuarto_hotel")
    void insertarCuartoHotel(
            @Param("p_idHotel") Integer idHotel,
            @Param("p_idTipoCuarto") Integer idTipoCuarto,
            @Param("p_numeroCuarto") Integer numeroCuarto,
            @Param("p_cantidadPersonas") Integer cantidadPersonas,
            @Param("p_precioNoche") BigDecimal precioNoche,
            @Param("p_estado") String estado
    );

    @Procedure(procedureName = "sp_update_cuarto_hotel")
    void actualizarCuartoHotel(
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_idTipoCuarto") Integer idTipoCuarto,
            @Param("p_numeroCuarto") Integer numeroCuarto,
            @Param("p_cantidadPersonas") Integer cantidadPersonas,
            @Param("p_precioNoche") BigDecimal precioNoche,
            @Param("p_estado") String estado
    );

    @Procedure(procedureName = "sp_delete_cuarto_hotel")
    void eliminarCuartoHotel(
            @Param("p_idCuartoHotel") Integer idCuartoHotel
    );
}