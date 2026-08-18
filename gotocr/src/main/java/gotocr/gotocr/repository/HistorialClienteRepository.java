package gotocr.gotocr.repository;


import gotocr.gotocr.domain.HistorialCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface HistorialClienteRepository extends JpaRepository<HistorialCliente, Integer> {

    @Procedure(procedureName = "sp_insert_historial_cliente")
    void insertarHistorialCliente(
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idReserva") Integer idReserva,
            @Param("p_accion") String accion
    );

    @Procedure(procedureName = "sp_update_historial_cliente")
    void actualizarHistorialCliente(
            @Param("p_idHistorialCliente") Integer idHistorialCliente,
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idReserva") Integer idReserva,
            @Param("p_accion") String accion
    );

    @Procedure(procedureName = "sp_delete_historial_cliente")
    void eliminarHistorialCliente(
            @Param("p_idHistorialCliente") Integer idHistorialCliente
    );
}