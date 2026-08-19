DROP PROCEDURE IF EXISTS sp_insert_resena_hotel;
DROP PROCEDURE IF EXISTS sp_update_resena_hotel;
DROP PROCEDURE IF EXISTS sp_delete_resena_hotel;
DROP PROCEDURE IF EXISTS sp_recalcular_calificacion_hotel;

DELIMITER $$

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

END $$


CREATE PROCEDURE sp_update_resena_hotel(
    IN p_idResena INT,
    IN p_calificacion INT,
    IN p_comentario VARCHAR(500)
)
BEGIN

    UPDATE RESENAHOTEL
    SET
        calificacion = p_calificacion,
        comentario = p_comentario
    WHERE idResena = p_idResena;

END $$


CREATE PROCEDURE sp_delete_resena_hotel(
    IN p_idResena INT
)
BEGIN

    DELETE FROM RESENAHOTEL
    WHERE idResena = p_idResena;

END $$


CREATE PROCEDURE sp_recalcular_calificacion_hotel(
    IN p_idHotel INT
)
BEGIN

    UPDATE HOTEL
    SET calificacionPromedio = (
        SELECT COALESCE(
            AVG(calificacion),
            0
        )
        FROM RESENAHOTEL
        WHERE idHotel = p_idHotel
    )
    WHERE idHotel = p_idHotel;

END $$

DELIMITER ;