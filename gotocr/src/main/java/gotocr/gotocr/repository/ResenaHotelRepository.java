package gotocr.gotocr.repository;

import gotocr.gotocr.domain.ResenaHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface ResenaHotelRepository
        extends JpaRepository<ResenaHotel, Integer> {

    @Query("""
           SELECT r
           FROM ResenaHotel r
           JOIN FETCH r.cliente c
           WHERE r.hotel.idHotel = :idHotel
           ORDER BY r.fecha DESC
           """)
    List<ResenaHotel> buscarPorHotel(
            @Param("idHotel") Integer idHotel
    );

    @Query("""
           SELECT r
           FROM ResenaHotel r
           WHERE r.hotel.idHotel = :idHotel
           AND r.cliente.idCliente = :idCliente
           """)
    Optional<ResenaHotel> buscarPorClienteYHotel(
            @Param("idCliente") Integer idCliente,
            @Param("idHotel") Integer idHotel
    );

    @Procedure(
            procedureName = "sp_insert_resena_hotel"
    )
    void insertarResena(
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_calificacion") Integer calificacion,
            @Param("p_comentario") String comentario
    );

    @Procedure(
            procedureName = "sp_update_resena_hotel"
    )
    void actualizarResena(
            @Param("p_idResena") Integer idResena,
            @Param("p_calificacion") Integer calificacion,
            @Param("p_comentario") String comentario
    );

    @Procedure(
            procedureName = "sp_delete_resena_hotel"
    )
    void eliminarResena(
            @Param("p_idResena") Integer idResena
    );
    
    
    @Procedure(
        procedureName = "sp_insert_resena_hotel"
)
void insertarResenaHotel(
        @Param("p_idCliente")
        Integer idCliente,

        @Param("p_idHotel")
        Integer idHotel,

        @Param("p_calificacion")
        Integer calificacion,

        @Param("p_comentario")
        String comentario
);
}
