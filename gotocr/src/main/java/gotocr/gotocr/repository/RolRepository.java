package gotocr.gotocr.repository;

import gotocr.gotocr.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

public interface RolRepository extends JpaRepository<Rol, Integer> {

    @Procedure(procedureName = "sp_insert_rol")
    void insertarRol(
            @Param("p_nombreRol") String nombreRol
    );

    @Procedure(procedureName = "sp_update_rol")
    void actualizarRol(
            @Param("p_idRol") Integer idRol,
            @Param("p_nombreRol") String nombreRol
    );

    @Procedure(procedureName = "sp_delete_rol")
    void eliminarRol(
            @Param("p_idRol") Integer idRol
    );
}