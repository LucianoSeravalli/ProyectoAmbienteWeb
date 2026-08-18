
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.TipoCuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TipoCuartoRepository extends JpaRepository<TipoCuarto, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_tipo_cuarto")
    void insertarTipoCuarto(
            @Param("p_nombreTipo") String nombreTipo,
            @Param("p_descripcion") String descripcion
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_tipo_cuarto")
    void actualizarTipoCuarto(
            @Param("p_idTipoCuarto") Integer idTipoCuarto,
            @Param("p_nombreTipo") String nombreTipo,
            @Param("p_descripcion") String descripcion
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_tipo_cuarto")
    void eliminarTipoCuarto(
            @Param("p_idTipoCuarto") Integer idTipoCuarto
    );

    // CONSULTAS

    @Query("SELECT t FROM TipoCuarto t")
    List<TipoCuarto> listarTiposCuarto();

    @Query("""
        SELECT t FROM TipoCuarto t
        WHERE t.idTipoCuarto = :idTipoCuarto
    """)
    Optional<TipoCuarto> buscarPorId(
            @Param("idTipoCuarto") Integer idTipoCuarto
    );

    @Query("""
        SELECT t FROM TipoCuarto t
        WHERE LOWER(t.nombreTipo) LIKE LOWER(CONCAT('%', :nombreTipo, '%'))
    """)
    List<TipoCuarto> buscarPorNombre(
            @Param("nombreTipo") String nombreTipo
    );
}