
package gotocr.gotocr.repository;


import gotocr.gotocr.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // INSERT
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

    // UPDATE
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

    // DELETE
    @Procedure(procedureName = "sp_delete_cliente")
    void eliminarCliente(
            @Param("p_idCliente") Integer idCliente
    );

    // CONSULTAS

    @Query("SELECT c FROM Cliente c")
    List<Cliente> listarClientes();

    @Query("SELECT c FROM Cliente c WHERE c.idCliente = :idCliente")
    Optional<Cliente> buscarPorId(
            @Param("idCliente") Integer idCliente
    );

    @Query("SELECT c FROM Cliente c WHERE c.correo = :correo")
    Optional<Cliente> buscarPorCorreo(
            @Param("correo") String correo
    );

    @Query("""
        SELECT c FROM Cliente c
        WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
    """)
    List<Cliente> buscarPorNombre(
            @Param("nombre") String nombre
    );

    @Query("""
        SELECT c FROM Cliente c
        WHERE c.rol.idRol = :idRol
    """)
    List<Cliente> buscarPorRol(
            @Param("idRol") Integer idRol
    );
}