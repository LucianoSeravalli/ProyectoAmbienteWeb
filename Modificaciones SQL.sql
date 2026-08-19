USE GOTOCR;

-- =====================================================
-- CLIENTE
-- =====================================================

ALTER TABLE CLIENTE
MODIFY COLUMN imagenPerfil MEDIUMBLOB NULL;

ALTER TABLE CLIENTE
ADD COLUMN tipoImagenPerfil VARCHAR(100) NULL
AFTER imagenPerfil;


-- =====================================================
-- HOTEL
-- =====================================================

ALTER TABLE HOTEL
MODIFY COLUMN imagenPrincipal MEDIUMBLOB NULL;

ALTER TABLE HOTEL
ADD COLUMN tipoImagenPrincipal VARCHAR(100) NULL
AFTER imagenPrincipal;


-- =====================================================
-- IMAGENCUARTO
-- =====================================================

ALTER TABLE IMAGENCUARTO
CHANGE COLUMN urlImagen imagen MEDIUMBLOB NOT NULL;

ALTER TABLE IMAGENCUARTO
ADD COLUMN tipoImagen VARCHAR(100) NOT NULL
AFTER imagen;