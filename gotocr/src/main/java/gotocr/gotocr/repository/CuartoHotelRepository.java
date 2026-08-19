package gotocr.gotocr.repository;

import gotocr.gotocr.domain.CuartoHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuartoHotelRepository
        extends JpaRepository<CuartoHotel, Integer> {

    @Query("""
           SELECT c
           FROM CuartoHotel c
           JOIN FETCH c.hotel h
           JOIN FETCH c.tipoCuarto t
           WHERE h.idHotel = :idHotel
           ORDER BY c.numeroCuarto
           """)
    List<CuartoHotel> buscarPorHotel(
            @Param("idHotel") Integer idHotel
    );

    @Procedure(
            procedureName = "sp_insert_cuarto_hotel"
    )
    void insertarCuartoHotel(
            @Param("p_idHotel") Integer idHotel,
            @Param("p_idTipoCuarto") Integer idTipoCuarto,
            @Param("p_numeroCuarto") Integer numeroCuarto,
            @Param("p_cantidadPersonas") Integer cantidadPersonas,
            @Param("p_precioNoche") BigDecimal precioNoche,
            @Param("p_estado") String estado
    );

    @Procedure(
            procedureName = "sp_update_cuarto_hotel"
    )
    void actualizarCuartoHotel(
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_idTipoCuarto") Integer idTipoCuarto,
            @Param("p_numeroCuarto") Integer numeroCuarto,
            @Param("p_cantidadPersonas") Integer cantidadPersonas,
            @Param("p_precioNoche") BigDecimal precioNoche,
            @Param("p_estado") String estado
    );

    @Procedure(
            procedureName = "sp_delete_cuarto_hotel"
    )
    void eliminarCuartoHotel(
            @Param("p_idCuartoHotel") Integer idCuartoHotel
    );

    @Query("""
       SELECT c
       FROM CuartoHotel c
       WHERE c.hotel.idHotel = :idHotel
       AND c.numeroCuarto = :numeroCuarto
       """)
    Optional<CuartoHotel> buscarPorHotelYNumero(
            @Param("idHotel") Integer idHotel,
            @Param("numeroCuarto") Integer numeroCuarto
    );
}
