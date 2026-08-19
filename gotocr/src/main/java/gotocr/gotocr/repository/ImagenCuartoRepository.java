package gotocr.gotocr.repository;

import gotocr.gotocr.domain.ImagenCuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;

public interface ImagenCuartoRepository extends JpaRepository<ImagenCuarto, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_imagen_cuarto")
    void insertarImagenCuarto(
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_urlImagen") String urlImagen
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_imagen_cuarto")
    void actualizarImagenCuarto(
            @Param("p_idImagen") Integer idImagen,
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_urlImagen") String urlImagen
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_imagen_cuarto")
    void eliminarImagenCuarto(
            @Param("p_idImagen") Integer idImagen
    );

    // CONSULTAS
    @Query("""
        SELECT i FROM ImagenCuarto i
        WHERE i.cuartoHotel.idCuartoHotel = :idCuartoHotel
    """)
    List<ImagenCuarto> buscarPorCuarto(
            @Param("idCuartoHotel") Integer idCuartoHotel
    );

    @Query("""
           SELECT i
           FROM ImagenCuarto i
           WHERE i.cuartoHotel.idCuartoHotel = :idCuartoHotel
           ORDER BY i.idImagen
           """)
    Optional<ImagenCuarto> buscarPrimeraPorCuarto(
            @Param("idCuartoHotel") Integer idCuartoHotel
    );

    @Modifying
    @Query("""
           DELETE FROM ImagenCuarto i
           WHERE i.cuartoHotel.idCuartoHotel = :idCuartoHotel
           """)
    void eliminarPorCuarto(
            @Param("idCuartoHotel") Integer idCuartoHotel
    );

}
