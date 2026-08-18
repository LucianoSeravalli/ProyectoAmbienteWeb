
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_reserva")
    void insertarReserva(
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_fechaEntrada") LocalDate fechaEntrada,
            @Param("p_fechaSalida") LocalDate fechaSalida,
            @Param("p_cantidadPersonas") Integer cantidadPersonas,
            @Param("p_precioTotal") BigDecimal precioTotal,
            @Param("p_estadoReserva") String estadoReserva
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_reserva")
    void actualizarReserva(
            @Param("p_idReserva") Integer idReserva,
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idHotel") Integer idHotel,
            @Param("p_idCuartoHotel") Integer idCuartoHotel,
            @Param("p_fechaEntrada") LocalDate fechaEntrada,
            @Param("p_fechaSalida") LocalDate fechaSalida,
            @Param("p_cantidadPersonas") Integer cantidadPersonas,
            @Param("p_precioTotal") BigDecimal precioTotal,
            @Param("p_estadoReserva") String estadoReserva
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_reserva")
    void eliminarReserva(
            @Param("p_idReserva") Integer idReserva
    );

    // CONSULTAS

    @Query("SELECT r FROM Reserva r")
    List<Reserva> listarReservas();

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.idReserva = :idReserva
    """)
    Optional<Reserva> buscarPorId(
            @Param("idReserva") Integer idReserva
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.cliente.idCliente = :idCliente
    """)
    List<Reserva> buscarPorCliente(
            @Param("idCliente") Integer idCliente
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.hotel.idHotel = :idHotel
    """)
    List<Reserva> buscarPorHotel(
            @Param("idHotel") Integer idHotel
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.cuartoHotel.idCuartoHotel = :idCuartoHotel
    """)
    List<Reserva> buscarPorCuarto(
            @Param("idCuartoHotel") Integer idCuartoHotel
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE LOWER(r.estadoReserva) = LOWER(:estado)
    """)
    List<Reserva> buscarPorEstado(
            @Param("estado") String estado
    );

    @Query("""
        SELECT r FROM Reserva r
        WHERE r.fechaEntrada >= :fechaInicio
        AND r.fechaSalida <= :fechaFin
    """)
    List<Reserva> buscarPorRangoFechas(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}