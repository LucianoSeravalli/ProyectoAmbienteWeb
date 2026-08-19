USE GOTOCR;

-- Este proyecto usa:
-- idRol = 1 -> usuario normal
-- idRol = 2 -> administrador
--
-- Ejecute los INSERT únicamente si todavía no existen estos roles.

INSERT INTO ROL (idRol, nombreRol)
SELECT 1, 'CLIENTE'
WHERE NOT EXISTS (
    SELECT 1
    FROM ROL
    WHERE idRol = 1
);

INSERT INTO ROL (idRol, nombreRol)
SELECT 2, 'ADMINISTRADOR'
WHERE NOT EXISTS (
    SELECT 1
    FROM ROL
    WHERE idRol = 2
);

-- Ejemplo para convertir un cliente existente en administrador:
-- UPDATE CLIENTE
-- SET idRol = 2
-- WHERE correo = 'admin@gotocr.com';
