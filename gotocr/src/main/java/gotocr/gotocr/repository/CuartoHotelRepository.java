
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.CuartoHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CuartoHotelRepository extends JpaRepository<CuartoHotel, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_cuarto_hotel")
    void insertarCuartoHotel(
            @Param("p_idHotel") Integer idHotel,
            @Param("p_idTipoCuarto") Integer idTipoCuarto,
            @Param("p_numeroCuarto") Integer numeroCuarto,
            @Param("p_cantidadPersonas") Integer cantidadPersonas,
            @Param("p_precioNoche") BigDecimal precioNoche,
            @Param("p_estado") String estado
    );

    // UPDATE
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

    // DELETE
    @Procedure(procedureName = "sp_delete_cuarto_hotel")
    void eliminarCuartoHotel(
            @Param("p_idCuartoHotel") Integer idCuartoHotel
    );

    // CONSULTAS

    @Query("SELECT c FROM CuartoHotel c")
    List<CuartoHotel> listarCuartos();

    @Query("""
        SELECT c FROM CuartoHotel c
        WHERE c.idCuartoHotel = :idCuartoHotel
    """)
    Optional<CuartoHotel> buscarPorId(
            @Param("idCuartoHotel") Integer idCuartoHotel
    );

    @Query("""
        SELECT c FROM CuartoHotel c
        WHERE c.hotel.idHotel = :idHotel
    """)
    List<CuartoHotel> buscarPorHotel(
            @Param("idHotel") Integer idHotel
    );

    @Query("""
        SELECT c FROM CuartoHotel c
        WHERE c.tipoCuarto.idTipoCuarto = :idTipoCuarto
    """)
    List<CuartoHotel> buscarPorTipo(
            @Param("idTipoCuarto") Integer idTipoCuarto
    );

    @Query("""
        SELECT c FROM CuartoHotel c
        WHERE c.hotel.idHotel = :idHotel
        AND LOWER(c.estado) = LOWER(:estado)
    """)
    List<CuartoHotel> buscarPorHotelYEstado(
            @Param("idHotel") Integer idHotel,
            @Param("estado") String estado
    );

    @Query("""
        SELECT c FROM CuartoHotel c
        WHERE c.cantidadPersonas >= :cantidadPersonas
    """)
    List<CuartoHotel> buscarPorCapacidad(
            @Param("cantidadPersonas") Integer cantidadPersonas
    );

    @Query("""
        SELECT c FROM CuartoHotel c
        WHERE c.precioNoche BETWEEN :precioMin AND :precioMax
    """)
    List<CuartoHotel> buscarPorRangoPrecio(
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax
    );
}