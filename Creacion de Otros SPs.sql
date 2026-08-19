USE GOTOCR;

DROP PROCEDURE IF EXISTS sp_insert_cuarto_hotel;
DROP PROCEDURE IF EXISTS sp_update_cuarto_hotel;
DROP PROCEDURE IF EXISTS sp_delete_cuarto_hotel;

DELIMITER $$


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
END $$



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
    WHERE idCuartoHotel =
          p_idCuartoHotel;
END $$



CREATE PROCEDURE sp_delete_cuarto_hotel(
    IN p_idCuartoHotel INT
)
BEGIN
    DELETE FROM CUARTOHOTEL
    WHERE idCuartoHotel =
          p_idCuartoHotel;
END $$
DELIMITER ;





DROP PROCEDURE IF EXISTS sp_recalcular_calificacion_hotel;

DELIMITER $$
CREATE PROCEDURE sp_recalcular_calificacion_hotel(
    IN p_idHotel INT
)
BEGIN
    UPDATE HOTEL
    SET calificacionPromedio = (
        SELECT COALESCE(AVG(calificacion),0)
        FROM RESENAHOTEL
        WHERE idHotel =
              p_idHotel
    )
    WHERE idHotel =
          p_idHotel;
END $$
DELIMITER ;


DROP PROCEDURE IF EXISTS sp_recalcular_cuartos_hotel;

DELIMITER $$
CREATE PROCEDURE sp_recalcular_cuartos_hotel(
    IN p_idHotel INT
)
BEGIN
    UPDATE HOTEL
    SET cuartosDisponibles = (
        SELECT COUNT(*)
        FROM CUARTOHOTEL
        WHERE idHotel = p_idHotel
    )
    WHERE idHotel = p_idHotel;
END $$
DELIMITER ;

