CREATE DATABASE GOTOCR
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE GOTOCR;

CREATE TABLE ROL (
    idRol INT AUTO_INCREMENT PRIMARY KEY,
    nombreRol VARCHAR(100) NOT NULL
) ENGINE=InnoDB;


-- =========================================================
-- 2. TABLA HOTEL
-- =========================================================

CREATE TABLE HOTEL (
    idHotel INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    imagenPrincipal VARCHAR(300),
    provincia VARCHAR(100),
    canton VARCHAR(100),
    direccion VARCHAR(300),
    telefono VARCHAR(30),
    calificacionPromedio DECIMAL(3,2) DEFAULT 0.00,
    cuartosDisponibles INT DEFAULT 0,
    estado VARCHAR(50) NOT NULL
) ENGINE=InnoDB;


-- =========================================================
-- 3. TABLA TIPO CUARTO
-- =========================================================

CREATE TABLE TIPOCUARTO (
    idTipoCuarto INT AUTO_INCREMENT PRIMARY KEY,
    nombreTipo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500)
) ENGINE=InnoDB;


-- =========================================================
-- 4. TABLA CLIENTE
-- =========================================================

CREATE TABLE CLIENTE (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    idRol INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(200) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    imagenPerfil VARCHAR(300),
    tokenConfirmacion VARCHAR(255),
    correoVerificado BOOLEAN DEFAULT FALSE,
    fechaRegistro DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_CLIENTE_ROL
        FOREIGN KEY (idRol)
        REFERENCES ROL(idRol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- =========================================================
-- 5. TABLA CUARTOHOTEL
-- =========================================================

CREATE TABLE CUARTOHOTEL (
    idCuartoHotel INT AUTO_INCREMENT PRIMARY KEY,
    idHotel INT NOT NULL,
    idTipoCuarto INT NOT NULL,
    numeroCuarto INT NOT NULL,
    cantidadPersonas INT NOT NULL,
    precioNoche DECIMAL(10,2) NOT NULL,
    estado VARCHAR(50) NOT NULL,

    CONSTRAINT FK_CUARTOHOTEL_HOTEL
        FOREIGN KEY (idHotel)
        REFERENCES HOTEL(idHotel)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_CUARTOHOTEL_TIPOCUARTO
        FOREIGN KEY (idTipoCuarto)
        REFERENCES TIPOCUARTO(idTipoCuarto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT UK_CUARTO_HOTEL_NUMERO
        UNIQUE (idHotel, numeroCuarto)
) ENGINE=InnoDB;


-- =========================================================
-- 6. TABLA IMAGENCUARTO
-- =========================================================

CREATE TABLE IMAGENCUARTO (
    idImagen INT AUTO_INCREMENT PRIMARY KEY,
    idCuartoHotel INT NOT NULL,
    urlImagen VARCHAR(500) NOT NULL,

    CONSTRAINT FK_IMAGEN_CUARTO
        FOREIGN KEY (idCuartoHotel)
        REFERENCES CUARTOHOTEL(idCuartoHotel)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 7. TABLA RESERVA
-- =========================================================

CREATE TABLE RESERVA (
    idReserva INT AUTO_INCREMENT PRIMARY KEY,
    idCliente INT NOT NULL,
    idHotel INT NOT NULL,
    idCuartoHotel INT NOT NULL,
    fechaEntrada DATE NOT NULL,
    fechaSalida DATE NOT NULL,
    cantidadPersonas INT NOT NULL,
    precioTotal DECIMAL(10,2) NOT NULL,
    estadoReserva VARCHAR(50) NOT NULL,
    fechaReserva DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_RESERVA_CLIENTE
        FOREIGN KEY (idCliente)
        REFERENCES CLIENTE(idCliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT FK_RESERVA_HOTEL
        FOREIGN KEY (idHotel)
        REFERENCES HOTEL(idHotel)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT FK_RESERVA_CUARTO
        FOREIGN KEY (idCuartoHotel)
        REFERENCES CUARTOHOTEL(idCuartoHotel)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT CHK_RESERVA_FECHAS
        CHECK (fechaSalida > fechaEntrada),

    CONSTRAINT CHK_RESERVA_PERSONAS
        CHECK (cantidadPersonas > 0)
) ENGINE=InnoDB;


-- =========================================================
-- 8. TABLA RESENAHOTEL
-- =========================================================

CREATE TABLE RESENAHOTEL (
    idResena INT AUTO_INCREMENT PRIMARY KEY,
    idCliente INT NOT NULL,
    idHotel INT NOT NULL,
    calificacion INT NOT NULL,
    comentario VARCHAR(500),
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_RESENA_CLIENTE
        FOREIGN KEY (idCliente)
        REFERENCES CLIENTE(idCliente)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_RESENA_HOTEL
        FOREIGN KEY (idHotel)
        REFERENCES HOTEL(idHotel)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT CHK_CALIFICACION
        CHECK (calificacion BETWEEN 1 AND 5)
) ENGINE=InnoDB;


-- =========================================================
-- 9. TABLA HISTORIALCLIENTE
-- =========================================================

CREATE TABLE HISTORIALCLIENTE (
    idHistorialCliente INT AUTO_INCREMENT PRIMARY KEY,
    idCliente INT NOT NULL,
    idReserva INT NOT NULL,
    accion VARCHAR(100) NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_HISTORIAL_CLIENTE
        FOREIGN KEY (idCliente)
        REFERENCES CLIENTE(idCliente)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT FK_HISTORIAL_RESERVA
        FOREIGN KEY (idReserva)
        REFERENCES RESERVA(idReserva)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) ENGINE=InnoDB;


-- =========================================================
-- 10. TABLA PAGO
-- =========================================================

CREATE TABLE PAGO (
    idPago INT AUTO_INCREMENT PRIMARY KEY,
    idReserva INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodoPago VARCHAR(50) NOT NULL,
    estadoPago VARCHAR(50) NOT NULL,
    fechaPago DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT FK_PAGO_RESERVA
        FOREIGN KEY (idReserva)
        REFERENCES RESERVA(idReserva)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT CHK_PAGO_MONTO
        CHECK (monto > 0)
) ENGINE=InnoDB;


-- =========================================================
-- ÍNDICES PARA FOREIGN KEYS / CONSULTAS
-- =========================================================

CREATE INDEX IDX_CLIENTE_ROL
    ON CLIENTE(idRol);

CREATE INDEX IDX_CUARTOHOTEL_HOTEL
    ON CUARTOHOTEL(idHotel);

CREATE INDEX IDX_CUARTOHOTEL_TIPO
    ON CUARTOHOTEL(idTipoCuarto);

CREATE INDEX IDX_IMAGEN_CUARTO
    ON IMAGENCUARTO(idCuartoHotel);

CREATE INDEX IDX_RESERVA_CLIENTE
    ON RESERVA(idCliente);

CREATE INDEX IDX_RESERVA_HOTEL
    ON RESERVA(idHotel);

CREATE INDEX IDX_RESERVA_CUARTO
    ON RESERVA(idCuartoHotel);

CREATE INDEX IDX_RESENA_CLIENTE
    ON RESENAHOTEL(idCliente);

CREATE INDEX IDX_RESENA_HOTEL
    ON RESENAHOTEL(idHotel);

CREATE INDEX IDX_HISTORIAL_CLIENTE
    ON HISTORIALCLIENTE(idCliente);

CREATE INDEX IDX_HISTORIAL_RESERVA
    ON HISTORIALCLIENTE(idReserva);

CREATE INDEX IDX_PAGO_RESERVA
    ON PAGO(idReserva);


-- =========================================================
-- VERIFICACIÓN
-- =========================================================

SHOW TABLES;