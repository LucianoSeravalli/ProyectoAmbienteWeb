USE GOTOCR;

-- =========================================== Creacion de SPs ======================================--
-- Rol
-- ================== Insertar Rol
DELIMITER //
CREATE PROCEDURE sp_insert_rol(
    IN p_nombreRol VARCHAR(100)
)
BEGIN
    INSERT INTO ROL (nombreRol)
    VALUES (
        p_nombreRol
    );
END //
DELIMITER ;

-- ================= Update Rol
DELIMITER //
CREATE PROCEDURE sp_update_rol(
    IN p_idRol INT,
    IN p_nombreRol VARCHAR(100)
)
BEGIN
    UPDATE ROL
    SET nombreRol = p_nombreRol
    WHERE idRol = p_idRol;
END //
DELIMITER ;

-- =================Delete Rol
DELIMITER //
CREATE PROCEDURE sp_delete_rol(IN p_idRol INT)
BEGIN
    DELETE FROM ROL
    WHERE idRol = p_idRol;
END //
DELIMITER ;



-- Clientes
-- ======================= Insert 
DELIMITER //
CREATE PROCEDURE sp_insert_cliente(
    IN p_idRol INT,
    IN p_nombre VARCHAR(100),
    IN p_apellido VARCHAR(100),
    IN p_correo VARCHAR(200),
    IN p_contrasena VARCHAR(255),
    IN p_imagenPerfil VARCHAR(300),
    IN p_tokenConfirmacion VARCHAR(255),
    IN p_correoVerificado BOOLEAN
)
BEGIN
    INSERT INTO CLIENTE (
        idRol,
        nombre,
        apellido,
        correo,
        contrasena,
        imagenPerfil,
        tokenConfirmacion,
        correoVerificado
    )
    VALUES (
        p_idRol,
        p_nombre,
        p_apellido,
        p_correo,
        p_contrasena,
        p_imagenPerfil,
        p_tokenConfirmacion,
        p_correoVerificado
    );
END //
DELIMITER ;


-- ========================= Actualizar
DELIMITER //
CREATE PROCEDURE sp_update_cliente(
    IN p_idCliente INT,
    IN p_idRol INT,
    IN p_nombre VARCHAR(100),
    IN p_apellido VARCHAR(100),
    IN p_correo VARCHAR(200),
    IN p_contrasena VARCHAR(255),
    IN p_imagenPerfil VARCHAR(300),
    IN p_tokenConfirmacion VARCHAR(255),
    IN p_correoVerificado BOOLEAN
)
BEGIN
    UPDATE CLIENTE
    SET
        idRol = p_idRol,
        nombre = p_nombre,
        apellido = p_apellido,
        correo = p_correo,
        contrasena = p_contrasena,
        imagenPerfil = p_imagenPerfil,
        tokenConfirmacion = p_tokenConfirmacion,
        correoVerificado = p_correoVerificado
    WHERE idCliente = p_idCliente;
END //
DELIMITER ;


-- =================== Eliminar
DELIMITER //
CREATE PROCEDURE sp_delete_cliente(
    IN p_idCliente INT
)
BEGIN
    DELETE FROM CLIENTE
    WHERE idCliente = p_idCliente;
END //
DELIMITER ;


-- Hotel
-- =========================== insertar Hotel
DELIMITER //
CREATE PROCEDURE sp_insert_hotel(
    IN p_nombre VARCHAR(150),
    IN p_descripcion VARCHAR(500),
    IN p_imagenPrincipal VARCHAR(300),
    IN p_provincia VARCHAR(100),
    IN p_canton VARCHAR(100),
    IN p_direccion VARCHAR(300),
    IN p_telefono VARCHAR(30),
    IN p_calificacionPromedio DECIMAL(3,2),
    IN p_cuartosDisponibles INT,
    IN p_estado VARCHAR(50)
)
BEGIN
    INSERT INTO HOTEL (
        nombre,
        descripcion,
        imagenPrincipal,
        provincia,
        canton,
        direccion,
        telefono,
        calificacionPromedio,
        cuartosDisponibles,
        estado
    )
    VALUES (
        p_nombre,
        p_descripcion,
        p_imagenPrincipal,
        p_provincia,
        p_canton,
        p_direccion,
        p_telefono,
        p_calificacionPromedio,
        p_cuartosDisponibles,
        p_estado
    );
END //
DELIMITER ;


-- ================= UPDATE
DELIMITER //

CREATE PROCEDURE sp_update_hotel(
    IN p_idHotel INT,
    IN p_nombre VARCHAR(150),
    IN p_descripcion VARCHAR(500),
    IN p_imagenPrincipal VARCHAR(300),
    IN p_provincia VARCHAR(100),
    IN p_canton VARCHAR(100),
    IN p_direccion VARCHAR(300),
    IN p_telefono VARCHAR(30),
    IN p_calificacionPromedio DECIMAL(3,2),
    IN p_cuartosDisponibles INT,
    IN p_estado VARCHAR(50)
)
BEGIN
    UPDATE HOTEL
    SET
        nombre = p_nombre,
        descripcion = p_descripcion,
        imagenPrincipal = p_imagenPrincipal,
        provincia = p_provincia,
        canton = p_canton,
        direccion = p_direccion,
        telefono = p_telefono,
        calificacionPromedio = p_calificacionPromedio,
        cuartosDisponibles = p_cuartosDisponibles,
        estado = p_estado
    WHERE idHotel = p_idHotel;
END //
DELIMITER ;


-- ====== Delte
DELIMITER //

CREATE PROCEDURE sp_delete_hotel(
    IN p_idHotel INT
)
BEGIN
    DELETE FROM HOTEL
    WHERE idHotel = p_idHotel;
END //

DELIMITER ;


-- Tipo Cuarto
-- ============ Insert
DELIMITER //

CREATE PROCEDURE sp_insert_tipo_cuarto(
    IN p_nombreTipo VARCHAR(100),
    IN p_descripcion VARCHAR(500)
)
BEGIN
    INSERT INTO TIPOCUARTO (
        nombreTipo,
        descripcion
    )
    VALUES (
        p_nombreTipo,
        p_descripcion
    );
END //

DELIMITER ;

-- =========== update

DELIMITER //

CREATE PROCEDURE sp_update_tipo_cuarto(
    IN p_idTipoCuarto INT,
    IN p_nombreTipo VARCHAR(100),
    IN p_descripcion VARCHAR(500)
)
BEGIN
    UPDATE TIPOCUARTO
    SET
        nombreTipo = p_nombreTipo,
        descripcion = p_descripcion
    WHERE idTipoCuarto = p_idTipoCuarto;
END //

DELIMITER ;

-- ============== Delete

DELIMITER //

CREATE PROCEDURE sp_delete_tipo_cuarto(
    IN p_idTipoCuarto INT
)
BEGIN
    DELETE FROM TIPOCUARTO
    WHERE idTipoCuarto = p_idTipoCuarto;
END //

DELIMITER ;



-- Cuarto Hotel
-- ========== Insert
DELIMITER //

CREATE PROCEDURE sp_insert_cuarto_hotel(
    IN p_idHotel INT,
    IN p_idTipoCuarto INT,
    IN p_numeroCuarto INT,
    IN p_cantidadPersonas INT,
    IN p_precioNoche DECIMAL(10,2),
    IN p_estado VARCHAR(50)
)
BEGIN
    INSERT INTO CUARTOHOTEL (
        idHotel,
        idTipoCuarto,
        numeroCuarto,
        cantidadPersonas,
        precioNoche,
        estado
    )
    VALUES (
        p_idHotel,
        p_idTipoCuarto,
        p_numeroCuarto,
        p_cantidadPersonas,
        p_precioNoche,
        p_estado
    );
END //

DELIMITER ;


-- ========= Update
DELIMITER //

CREATE PROCEDURE sp_update_cuarto_hotel(
    IN p_idCuartoHotel INT,
    IN p_idHotel INT,
    IN p_idTipoCuarto INT,
    IN p_numeroCuarto INT,
    IN p_cantidadPersonas INT,
    IN p_precioNoche DECIMAL(10,2),
    IN p_estado VARCHAR(50)
)
BEGIN
    UPDATE CUARTOHOTEL
    SET
        idHotel = p_idHotel,
        idTipoCuarto = p_idTipoCuarto,
        numeroCuarto = p_numeroCuarto,
        cantidadPersonas = p_cantidadPersonas,
        precioNoche = p_precioNoche,
        estado = p_estado
    WHERE idCuartoHotel = p_idCuartoHotel;
END //

DELIMITER ;


-- ========== Delete
DELIMITER //

CREATE PROCEDURE sp_delete_cuarto_hotel(
    IN p_idCuartoHotel INT
)
BEGIN
    DELETE FROM CUARTOHOTEL
    WHERE idCuartoHotel = p_idCuartoHotel;
END //

DELIMITER ;


-- ========= Imagen Cuarto
DELIMITER //
-- ==== Insertar
CREATE PROCEDURE sp_insert_imagen_cuarto(
    IN p_idCuartoHotel INT,
    IN p_urlImagen VARCHAR(500)
)
BEGIN
    INSERT INTO IMAGENCUARTO (
        idCuartoHotel,
        urlImagen
    )
    VALUES (
        p_idCuartoHotel,
        p_urlImagen
    );
END //

DELIMITER ;


-- ======= Actualizar
DELIMITER //

CREATE PROCEDURE sp_update_imagen_cuarto(
    IN p_idImagen INT,
    IN p_idCuartoHotel INT,
    IN p_urlImagen VARCHAR(500)
)
BEGIN
    UPDATE IMAGENCUARTO
    SET
        idCuartoHotel = p_idCuartoHotel,
        urlImagen = p_urlImagen
    WHERE idImagen = p_idImagen;
END //

DELIMITER ;


-- ======== Delete
DELIMITER //

CREATE PROCEDURE sp_delete_imagen_cuarto(
    IN p_idImagen INT
)
BEGIN
    DELETE FROM IMAGENCUARTO
    WHERE idImagen = p_idImagen;
END //

DELIMITER ;

-- Reserva
-- ============ Insertar
DELIMITER //

CREATE PROCEDURE sp_insert_reserva(
    IN p_idCliente INT,
    IN p_idHotel INT,
    IN p_idCuartoHotel INT,
    IN p_fechaEntrada DATE,
    IN p_fechaSalida DATE,
    IN p_cantidadPersonas INT,
    IN p_precioTotal DECIMAL(10,2),
    IN p_estadoReserva VARCHAR(50)
)
BEGIN
    INSERT INTO RESERVA (
        idCliente,
        idHotel,
        idCuartoHotel,
        fechaEntrada,
        fechaSalida,
        cantidadPersonas,
        precioTotal,
        estadoReserva
    )
    VALUES (
        p_idCliente,
        p_idHotel,
        p_idCuartoHotel,
        p_fechaEntrada,
        p_fechaSalida,
        p_cantidadPersonas,
        p_precioTotal,
        p_estadoReserva
    );
END //

DELIMITER ;


-- ===== Actualizar
DELIMITER //

CREATE PROCEDURE sp_update_reserva(
    IN p_idReserva INT,
    IN p_idCliente INT,
    IN p_idHotel INT,
    IN p_idCuartoHotel INT,
    IN p_fechaEntrada DATE,
    IN p_fechaSalida DATE,
    IN p_cantidadPersonas INT,
    IN p_precioTotal DECIMAL(10,2),
    IN p_estadoReserva VARCHAR(50)
)
BEGIN
    UPDATE RESERVA
    SET
        idCliente = p_idCliente,
        idHotel = p_idHotel,
        idCuartoHotel = p_idCuartoHotel,
        fechaEntrada = p_fechaEntrada,
        fechaSalida = p_fechaSalida,
        cantidadPersonas = p_cantidadPersonas,
        precioTotal = p_precioTotal,
        estadoReserva = p_estadoReserva
    WHERE idReserva = p_idReserva;
END //

DELIMITER ;

-- ======== Delete
DELIMITER //

CREATE PROCEDURE sp_delete_reserva(
    IN p_idReserva INT
)
BEGIN
    DELETE FROM RESERVA
    WHERE idReserva = p_idReserva;
END //

DELIMITER ;


-- ==== Resena Hotel
-- ============ Insert
DELIMITER //

CREATE PROCEDURE sp_insert_resena_hotel(
    IN p_idCliente INT,
    IN p_idHotel INT,
    IN p_calificacion INT,
    IN p_comentario VARCHAR(500)
)
BEGIN
    INSERT INTO RESENAHOTEL (
        idCliente,
        idHotel,
        calificacion,
        comentario
    )
    VALUES (
        p_idCliente,
        p_idHotel,
        p_calificacion,
        p_comentario
    );
END //

-- ========== Update

DELIMITER //

CREATE PROCEDURE sp_update_resena_hotel(
    IN p_idResena INT,
    IN p_idCliente INT,
    IN p_idHotel INT,
    IN p_calificacion INT,
    IN p_comentario VARCHAR(500)
)
BEGIN
    UPDATE RESENAHOTEL
    SET
        idCliente = p_idCliente,
        idHotel = p_idHotel,
        calificacion = p_calificacion,
        comentario = p_comentario
    WHERE idResena = p_idResena;
END //

DELIMITER ;


-- ========== Delete
DELIMITER //

CREATE PROCEDURE sp_delete_resena_hotel(
    IN p_idResena INT
)
BEGIN
    DELETE FROM RESENAHOTEL
    WHERE idResena = p_idResena;
END //

DELIMITER ;


-- Historial Cliente
DELIMITER //
-- ========= Insert
CREATE PROCEDURE sp_insert_historial_cliente(
    IN p_idCliente INT,
    IN p_idReserva INT,
    IN p_accion VARCHAR(100)
)
BEGIN
    INSERT INTO HISTORIALCLIENTE (
        idCliente,
        idReserva,
        accion
    )
    VALUES (
        p_idCliente,
        p_idReserva,
        p_accion
    );
END //

DELIMITER ;

-- ===== Update
DELIMITER //

CREATE PROCEDURE sp_update_historial_cliente(
    IN p_idHistorialCliente INT,
    IN p_idCliente INT,
    IN p_idReserva INT,
    IN p_accion VARCHAR(100)
)
BEGIN
    UPDATE HISTORIALCLIENTE
    SET
        idCliente = p_idCliente,
        idReserva = p_idReserva,
        accion = p_accion
    WHERE idHistorialCliente = p_idHistorialCliente;
END //

DELIMITER ;


-- ======== Delete
DELIMITER //

CREATE PROCEDURE sp_delete_historial_cliente(
    IN p_idHistorialCliente INT
)
BEGIN
    DELETE FROM HISTORIALCLIENTE
    WHERE idHistorialCliente = p_idHistorialCliente;
END //

DELIMITER ;

-- Pago

-- ========== iNSERT

DELIMITER //

CREATE PROCEDURE sp_insert_pago(
    IN p_idReserva INT,
    IN p_monto DECIMAL(10,2),
    IN p_metodoPago VARCHAR(50),
    IN p_estadoPago VARCHAR(50)
)
BEGIN
    INSERT INTO PAGO (
        idReserva,
        monto,
        metodoPago,
        estadoPago
    )
    VALUES (
        p_idReserva,
        p_monto,
        p_metodoPago,
        p_estadoPago
    );
END //

DELIMITER ;


-- ======= UPDATE
DELIMITER //

CREATE PROCEDURE sp_update_pago(
    IN p_idPago INT,
    IN p_idReserva INT,
    IN p_monto DECIMAL(10,2),
    IN p_metodoPago VARCHAR(50),
    IN p_estadoPago VARCHAR(50)
)
BEGIN
    UPDATE PAGO
    SET
        idReserva = p_idReserva,
        monto = p_monto,
        metodoPago = p_metodoPago,
        estadoPago = p_estadoPago
    WHERE idPago = p_idPago;
END //

DELIMITER ;


-- ======== Delete

DELIMITER //

CREATE PROCEDURE sp_delete_pago(
    IN p_idPago INT
)
BEGIN
    DELETE FROM PAGO
    WHERE idPago = p_idPago;
END //

DELIMITER ;




