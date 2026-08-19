let datosReserva = null;

document.addEventListener(
        "DOMContentLoaded",
        async () => {

    await cargarDatosReserva();

    document
            .querySelectorAll(
                    'input[name="metodoPago"]'
                    )
            .forEach(radio => {

                radio.addEventListener(
                        "change",
                        actualizarMetodoPago
                        );
            });

    document
            .getElementById("reservaForm")
            ?.addEventListener(
                    "submit",
                    confirmarReserva
                    );

    actualizarMetodoPago();
}
);


async function cargarDatosReserva() {

    const mensaje =
            document.getElementById(
                    "reservaMensaje"
                    );

    const idCuarto =
            document.body.dataset.cuartoId;

    const fechaEntrada =
            document.body.dataset.fechaEntrada;

    const fechaSalida =
            document.body.dataset.fechaSalida;

    const personas =
            Number(
                    document.body.dataset.personas || 1
                    );


    console.log("=== DATOS RESERVA ===");
    console.log("Cuarto:", idCuarto);
    console.log("Entrada:", fechaEntrada);
    console.log("Salida:", fechaSalida);
    console.log("Personas:", personas);


    if (!idCuarto) {

        mostrarMensaje(
                mensaje,
                "No se recibió el cuarto seleccionado."
                );

        return;
    }


    if (!fechaEntrada || !fechaSalida) {

        mostrarMensaje(
                mensaje,
                "Debe seleccionar las fechas de entrada y salida."
                );

        return;
    }


    try {

        const respuesta =
                await fetch(
                        `/reserva/datos/${idCuarto}`
                        );


        if (respuesta.status === 401) {

            window.location.href =
                    "/login";

            return;
        }


        if (!respuesta.ok) {

            throw new Error(
                    "No se pudieron cargar los datos de la reserva."
                    );
        }


        datosReserva =
                await respuesta.json();


        console.log(
                "Datos recibidos:",
                datosReserva
                );


        const cliente =
                datosReserva.cliente;

        const cuarto =
                datosReserva.cuarto;

        const hotel =
                datosReserva.hotel;


        // ============================
        // DATOS CLIENTE
        // ============================

        document.getElementById(
                "nombreHuesped"
                ).value =
                cliente.nombre ?? "";


        document.getElementById(
                "apellidoHuesped"
                ).value =
                cliente.apellido ?? "";


        document.getElementById(
                "correoHuesped"
                ).value =
                cliente.correo ?? "";


        // ============================
        // HOTEL
        // ============================

        document.getElementById(
                "resumenHotelNombre"
                ).textContent =
                hotel.nombre ?? "Hotel";


        const ubicacion = [
            hotel.canton,
            hotel.provincia
        ]
                .filter(Boolean)
                .join(", ");


        document.getElementById(
                "resumenHotelUbicacion"
                ).innerHTML = `

            <i class="bi bi-geo-alt"></i>

            ${escaparHtml(ubicacion)}

        `;


        const imagenHotel =
                document.getElementById(
                        "resumenHotelImagen"
                        );


        if (imagenHotel) {

            imagenHotel.src =
                    hotel.tieneImagen
                    ? `/hoteles/imagen/${hotel.idHotel}`
                    : "/img/hotel-default.jpg";


            imagenHotel.onerror =
                    function () {

                        this.onerror = null;

                        this.src =
                                "/img/hotel-default.jpg";
                    };
        }


        // ============================
        // CUARTO
        // ============================

        document.getElementById(
                "resumenCuarto"
                ).textContent =
                cuarto.tipoCuarto
                ?? `Cuarto #${cuarto.numeroCuarto}`;


        // ============================
        // FECHAS
        // ============================

        document.getElementById(
                "resumenEntrada"
                ).textContent =
                formatoFecha(fechaEntrada);


        document.getElementById(
                "resumenSalida"
                ).textContent =
                formatoFecha(fechaSalida);


        document.getElementById(
                "resumenHuespedes"
                ).textContent =
                `${personas} ${
                personas === 1
                ? "persona"
                : "personas"
                }`;


        calcularResumen(
                fechaEntrada,
                fechaSalida,
                cuarto.precioNoche
                );


    } catch (error) {

        console.error(
                "ERROR CARGANDO RESERVA:",
                error
                );


        mostrarMensaje(
                mensaje,
                error.message
                );
    }
}


function calcularResumen(
        entrada,
        salida,
        precioNoche
        ) {

    const fecha1 =
            new Date(
                    `${entrada}T00:00:00`
                    );

    const fecha2 =
            new Date(
                    `${salida}T00:00:00`
                    );


    const noches =
            Math.max(
                    0,
                    Math.round(
                            (fecha2 - fecha1)
                            /
                86400000
                            )
                    );


    const precio =
            Number(
                    precioNoche || 0
                    );


    const subtotal =
            precio * noches;


    const impuestos =
            subtotal * 0.12;


    const total =
            subtotal + impuestos;


    document.getElementById(
            "resumenCalculoNoches"
            ).textContent =
            `${formatoMoneda(precio)} x ${noches} noches`;


    document.getElementById(
            "resumenSubtotal"
            ).textContent =
            formatoMoneda(subtotal);


    document.getElementById(
            "resumenImpuestos"
            ).textContent =
            formatoMoneda(impuestos);


    document.getElementById(
            "resumenTotal"
            ).textContent =
            formatoMoneda(total);
}


function actualizarMetodoPago() {

    const metodo =
            document.querySelector(
                    'input[name="metodoPago"]:checked'
                    )?.value;


    const camposTarjeta =
            document.getElementById(
                    "camposTarjeta"
                    );


    if (!camposTarjeta) {
        return;
    }


    camposTarjeta.classList.toggle(
            "d-none",
            metodo !== "TARJETA"
            );
}


async function confirmarReserva(
        evento
        ) {

    evento.preventDefault();


    const mensaje =
            document.getElementById(
                    "reservaMensaje"
                    );


    /*
     * IMPORTANTE:
     *
     * Estos datos vienen del BODY.
     * NO buscamos inputs que no existen.
     */
    const idCuartoHotel =
            document.body.dataset.cuartoId;


    const fechaEntrada =
            document.body.dataset.fechaEntrada;


    const fechaSalida =
            document.body.dataset.fechaSalida;


    const cantidadPersonas =
            document.body.dataset.personas
            || "1";


    console.log(
            "=== CONFIRMANDO RESERVA ==="
            );

    console.log(
            "idCuartoHotel:",
            idCuartoHotel
            );

    console.log(
            "fechaEntrada:",
            fechaEntrada
            );

    console.log(
            "fechaSalida:",
            fechaSalida
            );

    console.log(
            "cantidadPersonas:",
            cantidadPersonas
            );


    if (!idCuartoHotel) {

        mostrarMensaje(
                mensaje,
                "No se recibió el cuarto."
                );

        return;
    }


    if (!fechaEntrada) {

        mostrarMensaje(
                mensaje,
                "No se recibió la fecha de entrada."
                );

        return;
    }


    if (!fechaSalida) {

        mostrarMensaje(
                mensaje,
                "No se recibió la fecha de salida."
                );

        return;
    }


    if (
            new Date(fechaSalida)
            <=
            new Date(fechaEntrada)
            ) {

        mostrarMensaje(
                mensaje,
                "La fecha de salida debe ser posterior a la fecha de entrada."
                );

        return;
    }


    const metodoSeleccionado =
            document.querySelector(
                    'input[name="metodoPago"]:checked'
                    );


    if (!metodoSeleccionado) {

        mostrarMensaje(
                mensaje,
                "Seleccione un método de pago."
                );

        return;
    }


    const metodoPago =
            metodoSeleccionado.value;


    const datos =
            new URLSearchParams();


    datos.append(
            "idCuartoHotel",
            idCuartoHotel
            );


    datos.append(
            "fechaEntrada",
            fechaEntrada
            );


    datos.append(
            "fechaSalida",
            fechaSalida
            );


    datos.append(
            "cantidadPersonas",
            cantidadPersonas
            );


    datos.append(
            "metodoPago",
            metodoPago
            );


    console.log(
            "Enviando POST /reserva/confirmar"
            );


    try {

        const respuesta =
                await fetch(
                        "/reserva/confirmar",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                        "application/x-www-form-urlencoded"
                            },

                            body: datos.toString()
                        }
                );


        const texto =
                await respuesta.text();


        console.log(
                "HTTP:",
                respuesta.status
                );


        console.log(
                "Respuesta:",
                texto
                );


        let resultado = {};


        try {

            resultado =
                    texto
                    ? JSON.parse(texto)
                    : {};

        } catch (e) {

            throw new Error(
                    "El servidor no devolvió JSON. Revisá la consola de Spring Boot."
                    );
        }


        if (!respuesta.ok) {

            throw new Error(
                    resultado.error
                    ||
                    resultado.mensaje
                    ||
                    "No fue posible realizar la reserva."
                    );
        }


        alert(
                `Reserva #${resultado.idReserva} realizada correctamente`
                );


        window.location.href =
                "/historial";


    } catch (error) {

        console.error(
                "ERROR CONFIRMANDO RESERVA:",
                error
                );


        mostrarMensaje(
                mensaje,
                error.message
                );
    }
}