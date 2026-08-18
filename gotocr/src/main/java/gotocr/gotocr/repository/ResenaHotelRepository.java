package gotocr.gotocr.repository;

import gotocr.gotocr.domain.ResenaHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface ResenaHotelRepository extends JpaRepository<ResenaHotel, Integer> {

    @Procedure(procedureName = "sp_insert_resena_hotel")
    void insertarResenaHotel(
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_calificacion") Integer calificacion,
            @Param("p_comentario") String comentario
    );

    @Procedure(procedureName = "sp_update_resena_hotel")
    void actualizarResenaHotel(
            @Param("p_idResena") Integer idResena,
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_calificacion") Integer calificacion,
            @Param("p_comentario") String comentario
    );

    @Procedure(procedureName = "sp_delete_resena_hotel")
    void eliminarResenaHotel(
            @Param("p_idResena") Integer idResena
    );
}