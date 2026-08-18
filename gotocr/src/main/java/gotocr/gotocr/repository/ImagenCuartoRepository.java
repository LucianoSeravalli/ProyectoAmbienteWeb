
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.ImagenCuarto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface ImagenCuartoRepository extends JpaRepository<ImagenCuarto, Integer> {

    @Procedure(procedureName = "sp_insert_imagen_cuarto")
    void insertarImagenCuarto(
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_urlImagen") String urlImagen
    );

    @Procedure(procedureName = "sp_update_imagen_cuarto")
    void actualizarImagenCuarto(
            @Param("p_idImagen") Integer idImagen,
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_urlImagen") String urlImagen
    );

    @Procedure(procedureName = "sp_delete_imagen_cuarto")
    void eliminarImagenCuarto(
            @Param("p_idImagen") Integer idImagen
    );
}