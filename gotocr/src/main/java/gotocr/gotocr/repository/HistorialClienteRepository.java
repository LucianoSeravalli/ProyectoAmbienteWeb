package gotocr.gotocr.repository;

import gotocr.gotocr.domain.HistorialCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HistorialClienteRepository extends JpaRepository<HistorialCliente, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_historial_cliente")
    void insertarHistorialCliente(
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idReserva") Integer idReserva,
            @Param("p_accion") String accion
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_historial_cliente")
    void actualizarHistorialCliente(
            @Param("p_idHistorialCliente") Integer idHistorialCliente,
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idReserva") Integer idReserva,
            @Param("p_accion") String accion
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_historial_cliente")
    void eliminarHistorialCliente(
            @Param("p_idHistorialCliente") Integer idHistorialCliente
    );

    // CONSULTAS

    @Query("SELECT h FROM HistorialCliente h")
    List<HistorialCliente> listarHistorial();

    @Query("""
        SELECT h FROM HistorialCliente h
        WHERE h.cliente.idCliente = :idCliente
        ORDER BY h.fecha DESC
    """)
    List<HistorialCliente> buscarPorCliente(
            @Param("idCliente") Integer idCliente
    );

    @Query("""
        SELECT h FROM HistorialCliente h
        WHERE h.reserva.idReserva = :idReserva
    """)
    List<HistorialCliente> buscarPorReserva(
            @Param("idReserva") Integer idReserva
    );

    @Query("""
        SELECT h FROM HistorialCliente h
        WHERE LOWER(h.accion) = LOWER(:accion)
    """)
    List<HistorialCliente> buscarPorAccion(
            @Param("accion") String accion
    );
}