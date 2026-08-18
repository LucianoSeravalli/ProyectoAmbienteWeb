
package gotocr.gotocr.repository;

import gotocr.gotocr.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    @Procedure(procedureName = "sp_insert_cliente")
    void insertarCliente(
            @Param("p_idRol") Integer idRol,
            @Param("p_nombre") String nombre,
            @Param("p_apellido") String apellido,
            @Param("p_correo") String correo,
            @Param("p_contrasena") String contrasena,
            @Param("p_imagenPerfil") String imagenPerfil,
            @Param("p_tokenConfirmacion") String tokenConfirmacion,
            @Param("p_correoVerificado") Boolean correoVerificado
    );

    @Procedure(procedureName = "sp_update_cliente")
    void actualizarCliente(
            @Param("p_idCliente") Integer idCliente,
            @Param("p_idRol") Integer idRol,
            @Param("p_nombre") String nombre,
            @Param("p_apellido") String apellido,
            @Param("p_correo") String correo,
            @Param("p_contrasena") String contrasena,
            @Param("p_imagenPerfil") String imagenPerfil,
            @Param("p_tokenConfirmacion") String tokenConfirmacion,
            @Param("p_correoVerificado") Boolean correoVerificado
    );

    @Procedure(procedureName = "sp_delete_cliente")
    void eliminarCliente(
            @Param("p_idCliente") Integer idCliente
    );
}