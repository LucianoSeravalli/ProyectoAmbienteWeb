package gotocr.gotocr.repository;


import gotocr.gotocr.domain.ResenaHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResenaHotelRepository extends JpaRepository<ResenaHotel, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_resena_hotel")
    void insertarResenaHotel(
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_calificacion") Integer calificacion,
            @Param("p_comentario") String comentario
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_resena_hotel")
    void actualizarResenaHotel(
            @Param("p_idResena") Integer idResena,
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_calificacion") Integer calificacion,
            @Param("p_comentario") String comentario
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_resena_hotel")
    void eliminarResenaHotel(
            @Param("p_idResena") Integer idResena
    );

    // CONSULTAS

    @Query("SELECT r FROM ResenaHotel r")
    List<ResenaHotel> listarResenas();

    @Query("""
        SELECT r FROM ResenaHotel r
        WHERE r.idResena = :idResena
    """)
    Optional<ResenaHotel> buscarPorId(
            @Param("idResena") Integer idResena
    );

    @Query("""
        SELECT r FROM ResenaHotel r
        WHERE r.hotel.idHotel = :idHotel
    """)
    List<ResenaHotel> buscarPorHotel(
            @Param("idHotel") Integer idHotel
    );

    @Query("""
        SELECT r FROM ResenaHotel r
        WHERE r.cliente.idCliente = :idCliente
    """)
    List<ResenaHotel> buscarPorCliente(
            @Param("idCliente") Integer idCliente
    );

    @Query("""
        SELECT r FROM ResenaHotel r
        WHERE r.calificacion = :calificacion
    """)
    List<ResenaHotel> buscarPorCalificacion(
            @Param("calificacion") Integer calificacion
    );
}