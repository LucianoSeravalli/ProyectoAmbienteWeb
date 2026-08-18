package gotocr.gotocr.repository;


import gotocr.gotocr.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Integer> {
    
    

    // INSERT
    @Procedure(procedureName = "sp_insert_pago")
    void insertarPago(
            @Param("p_idReserva") Integer idReserva,
            @Param("p_monto") BigDecimal monto,
            @Param("p_metodoPago") String metodoPago,
            @Param("p_estadoPago") String estadoPago
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_pago")
    void actualizarPago(
            @Param("p_idPago") Integer idPago,
            @Param("p_idReserva") Integer idReserva,
            @Param("p_monto") BigDecimal monto,
            @Param("p_metodoPago") String metodoPago,
            @Param("p_estadoPago") String estadoPago
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_pago")
    void eliminarPago(
            @Param("p_idPago") Integer idPago
    );

    // CONSULTAS

    @Query("SELECT p FROM Pago p")
    List<Pago> listarPagos();

    @Query("""
        SELECT p FROM Pago p
        WHERE p.idPago = :idPago
    """)
    Optional<Pago> buscarPorId(
            @Param("idPago") Integer idPago
    );

    @Query("""
        SELECT p FROM Pago p
        WHERE p.reserva.idReserva = :idReserva
    """)
    List<Pago> buscarPorReserva(
            @Param("idReserva") Integer idReserva
    );

    @Query("""
        SELECT p FROM Pago p
        WHERE LOWER(p.estadoPago) = LOWER(:estadoPago)
    """)
    List<Pago> buscarPorEstado(
            @Param("estadoPago") String estadoPago
    );

    @Query("""
        SELECT p FROM Pago p
        WHERE LOWER(p.metodoPago) = LOWER(:metodoPago)
    """)
    List<Pago> buscarPorMetodoPago(
            @Param("metodoPago") String metodoPago
    );
}