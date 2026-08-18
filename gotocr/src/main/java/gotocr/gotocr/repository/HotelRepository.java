
package gotocr.gotocr.repository;


import gotocr.gotocr.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_hotel")
    void insertarHotel(
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_imagenPrincipal") String imagenPrincipal,
            @Param("p_provincia") String provincia,
            @Param("p_canton") String canton,
            @Param("p_direccion") String direccion,
            @Param("p_telefono") String telefono,
            @Param("p_calificacionPromedio") BigDecimal calificacionPromedio,
            @Param("p_cuartosDisponibles") Integer cuartosDisponibles,
            @Param("p_estado") String estado
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_hotel")
    void actualizarHotel(
            @Param("p_idHotel") Integer idHotel,
            @Param("p_nombre") String nombre,
            @Param("p_descripcion") String descripcion,
            @Param("p_imagenPrincipal") String imagenPrincipal,
            @Param("p_provincia") String provincia,
            @Param("p_canton") String canton,
            @Param("p_direccion") String direccion,
            @Param("p_telefono") String telefono,
            @Param("p_calificacionPromedio") BigDecimal calificacionPromedio,
            @Param("p_cuartosDisponibles") Integer cuartosDisponibles,
            @Param("p_estado") String estado
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_hotel")
    void eliminarHotel(
            @Param("p_idHotel") Integer idHotel
    );

    // CONSULTAS

    @Query("SELECT h FROM Hotel h")
    List<Hotel> listarHoteles();

    @Query("SELECT h FROM Hotel h WHERE h.idHotel = :idHotel")
    Optional<Hotel> buscarPorId(
            @Param("idHotel") Integer idHotel
    );

    @Query("""
        SELECT h FROM Hotel h
        WHERE LOWER(h.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
    """)
    List<Hotel> buscarPorNombre(
            @Param("nombre") String nombre
    );

    @Query("""
        SELECT h FROM Hotel h
        WHERE LOWER(h.provincia) = LOWER(:provincia)
    """)
    List<Hotel> buscarPorProvincia(
            @Param("provincia") String provincia
    );

    @Query("""
        SELECT h FROM Hotel h
        WHERE LOWER(h.canton) = LOWER(:canton)
    """)
    List<Hotel> buscarPorCanton(
            @Param("canton") String canton
    );

    @Query("""
        SELECT h FROM Hotel h
        WHERE LOWER(h.estado) = LOWER(:estado)
    """)
    List<Hotel> buscarPorEstado(
            @Param("estado") String estado
    );

    @Query("""
        SELECT h FROM Hotel h
        WHERE h.calificacionPromedio >= :calificacion
    """)
    List<Hotel> buscarPorCalificacionMinima(
            @Param("calificacion") BigDecimal calificacion
    );
}