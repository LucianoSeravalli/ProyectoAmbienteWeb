# GoToCR - MVC + JavaScript

Este paquete contiene las modificaciones solicitadas para usar:

- `@Controller` de Spring MVC.
- `Model` únicamente como puente cuando la vista necesita un identificador inicial.
- `@ResponseBody` en métodos concretos del mismo `@Controller` para que `fetch()` reciba JSON.
- JavaScript para cargar e inyectar hoteles, cuartos, reseñas, perfil, historial y resumen de reserva.
- Formularios tradicionales para login/registro y `fetch()` para perfil/reserva.

## Estructura

- `src/main/resources/templates/`: 8 HTML modificados.
- `src/main/resources/static/js/`: JavaScript de cada vista + `global.js`.
- `src/main/java/com/gotocr/controller/`: Controllers ajustados.

## Importante

1. Cambie `package com.gotocr.controller;` por el package real de su proyecto si es diferente.
2. Los CSS e imágenes deben permanecer en `src/main/resources/static/css` y `src/main/resources/static/img`.
3. Los valores guardados en `imagenPrincipal`, `imagenPerfil` y `urlImagen` pueden ser `img/archivo.jpg` o `/img/archivo.jpg`; `global.js` normaliza ambas formas.
4. El campo teléfono visible en `perfil.html` queda deshabilitado porque la tabla `CLIENTE` actual no tiene columna `telefono`.
5. El flujo de reserva guarda la `RESERVA`, pero todavía no inserta `PAGO` porque el SP `sp_insert_reserva` actual no devuelve `idReserva`. Ese cambio debe hacerse antes de guardar el pago de forma segura.
6. Para probar registro debe existir el rol `CLIENTE`:
   `INSERT INTO ROL(nombreRol) VALUES ('CLIENTE');`
7. Actualmente las contraseñas se comparan como texto porque así está construido el modelo actual. Para una implementación real se debe usar hash/Spring Security.

## Flujo de datos

Ejemplo de hoteles:

`GET /hoteles` -> devuelve `hoteles.html`

`hoteles.js` -> `fetch('/hoteles/datos')`

`HotelController.obtenerHoteles()` -> Service -> Repository -> MySQL

El método tiene `@ResponseBody`, pero la clase sigue siendo `@Controller`.
