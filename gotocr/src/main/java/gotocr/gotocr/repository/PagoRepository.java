package gotocr.gotocr.repository;


import gotocr.gotocr.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    @Procedure(procedureName = "sp_insert_pago")
    void insertarPago(
            @Param("p_idReserva") Integer idReserva,
            @Param("p_monto") BigDecimal monto,
            @Param("p_metodoPago") String metodoPago,
            @Param("p_estadoPago") String estadoPago
    );

    @Procedure(procedureName = "sp_update_pago")
    void actualizarPago(
            @Param("p_idPago") Integer idPago,
            @Param("p_idReserva") Integer idReserva,
            @Param("p_monto") BigDecimal monto,
            @Param("p_metodoPago") String metodoPago,
            @Param("p_estadoPago") String estadoPago
    );

    @Procedure(procedureName = "sp_delete_pago")
    void eliminarPago(
            @Param("p_idPago") Integer idPago
    );
}