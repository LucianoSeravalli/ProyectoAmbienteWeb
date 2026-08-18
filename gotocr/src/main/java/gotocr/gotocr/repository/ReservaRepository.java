
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

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

    @Procedure(procedureName = "sp_delete_reserva")
    void eliminarReserva(
            @Param("p_idReserva") Integer idReserva
    );
}