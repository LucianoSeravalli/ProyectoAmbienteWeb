
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.TipoCuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface TipoCuartoRepository extends JpaRepository<TipoCuarto, Integer> {

    @Procedure(procedureName = "sp_insert_tipo_cuarto")
    void insertarTipoCuarto(
            @Param("p_nombreTipo") String nombreTipo,
            @Param("p_descripcion") String descripcion
    );

    @Procedure(procedureName = "sp_update_tipo_cuarto")
    void actualizarTipoCuarto(
            @Param("p_idTipoCuarto") Integer idTipoCuarto,
            @Param("p_nombreTipo") String nombreTipo,
            @Param("p_descripcion") String descripcion
    );

    @Procedure(procedureName = "sp_delete_tipo_cuarto")
    void eliminarTipoCuarto(
            @Param("p_idTipoCuarto") Integer idTipoCuarto
    );
}