package gotocr.gotocr.repository;

import gotocr.gotocr.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    // INSERT
    @Procedure(procedureName = "sp_insert_rol")
    void insertarRol(
            @Param("p_nombreRol") String nombreRol
    );

    // UPDATE
    @Procedure(procedureName = "sp_update_rol")
    void actualizarRol(
            @Param("p_idRol") Integer idRol,
            @Param("p_nombreRol") String nombreRol
    );

    // DELETE
    @Procedure(procedureName = "sp_delete_rol")
    void eliminarRol(
            @Param("p_idRol") Integer idRol
    );

    // CONSULTAS
    @Query("SELECT r FROM Rol r")
    List<Rol> listarRoles();

    @Query("SELECT r FROM Rol r WHERE r.idRol = :idRol")
    Optional<Rol> buscarPorId(
            @Param("idRol") Integer idRol
    );

    @Query("SELECT r FROM Rol r WHERE r.nombreRol = :nombreRol")
    Optional<Rol> buscarPorNombre(
            @Param("nombreRol") String nombreRol
    );
}