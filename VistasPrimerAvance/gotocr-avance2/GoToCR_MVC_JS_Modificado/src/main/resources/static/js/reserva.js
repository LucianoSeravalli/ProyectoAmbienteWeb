let datosReserva = null;

document.addEventListener("DOMContentLoaded", async () => {
    await cargarDatosReserva();

    document.querySelectorAll('input[name="metodoPago"]').forEach(radio => {
        radio.addEventListener("change", actualizarMetodoPago);
    });

    document.getElementById("reservaForm")?.addEventListener("submit", confirmarReserva);
    actualizarMetodoPago();
});

async function cargarDatosReserva() {
    const idCuarto = document.body.dataset.cuartoId;
    const fechaEntrada = document.body.dataset.fechaEntrada;
    const fechaSalida = document.body.dataset.fechaSalida;
    const personas = Number(document.body.dataset.personas || 1);

    if (!idCuarto) {
        mostrarMensaje(document.getElementById("reservaMensaje"), "No se recibió el cuarto seleccionado.");
        return;
    }

    try {
        const respuesta = await fetch(`/reserva/datos/${idCuarto}`);

        if (respuesta.status === 401) {
            window.location.href = "/login";
            return;
        }

        if (!respuesta.ok) throw new Error("No se pudieron cargar los datos de la reserva.");

        datosReserva = await respuesta.json();

        const cliente = datosReserva.cliente;
        const cuarto = datosReserva.cuarto;
        const hotel = datosReserva.hotel;

        document.getElementById("nombreHuesped").value = cliente.nombre;
        document.getElementById("apellidoHuesped").value = cliente.apellido;
        document.getElementById("correoHuesped").value = cliente.correo;

        document.getElementById("resumenHotelNombre").textContent = hotel.nombre;
        document.getElementById("resumenHotelUbicacion").innerHTML =
            `<i class="bi bi-geo-alt"></i> ${escaparHtml(
                [hotel.canton, hotel.provincia].filter(Boolean).join(", ")
            )}`;
        document.getElementById("resumenHotelImagen").src =
            rutaImagen(hotel.imagenPrincipal);

        document.getElementById("resumenCuarto").textContent = cuarto.tipoCuarto;
        document.getElementById("resumenEntrada").textContent = formatoFecha(fechaEntrada);
        document.getElementById("resumenSalida").textContent = formatoFecha(fechaSalida);
        document.getElementById("resumenHuespedes").textContent = `${personas} personas`;

        calcularResumen(fechaEntrada, fechaSalida, cuarto.precioNoche);
    } catch (error) {
        console.error(error);
        mostrarMensaje(document.getElementById("reservaMensaje"), error.message);
    }
}

function calcularResumen(entrada, salida, precioNoche) {
    const noches = Math.max(
        0,
        Math.round(
            (new Date(`${salida}T00:00:00`) - new Date(`${entrada}T00:00:00`)) / 86400000
        )
    );

    const subtotal = Number(precioNoche || 0) * noches;
    const impuestos = subtotal * 0.12;
    const total = subtotal + impuestos;

    document.getElementById("resumenCalculoNoches").textContent =
        `${formatoMoneda(precioNoche)} x ${noches} noches`;
    document.getElementById("resumenSubtotal").textContent = formatoMoneda(subtotal);
    document.getElementById("resumenImpuestos").textContent = formatoMoneda(impuestos);
    document.getElementById("resumenTotal").textContent = formatoMoneda(total);
}

function actualizarMetodoPago() {
    const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value;
    const camposTarjeta = document.getElementById("camposTarjeta");

    if (!camposTarjeta) return;

    camposTarjeta.classList.toggle("d-none", metodo !== "TARJETA");
}

async function confirmarReserva(evento) {
    evento.preventDefault();

    const mensaje = document.getElementById("reservaMensaje");

    if (!datosReserva) {
        mostrarMensaje(mensaje, "No se pudo obtener la información de la reserva.");
        return;
    }

    const telefono = document.getElementById("telefonoHuesped").value.trim();
    const aceptaTerminos = document.getElementById("aceptaTerminos").checked;
    const metodoPago = document.querySelector('input[name="metodoPago"]:checked')?.value;
    const fechaEntrada = document.body.dataset.fechaEntrada;
    const fechaSalida = document.body.dataset.fechaSalida;
    const cantidadPersonas = Number(document.body.dataset.personas || 1);

    if (!telefono) {
        mostrarMensaje(mensaje, "El teléfono del huésped es obligatorio.");
        return;
    }

    if (!metodoPago) {
        mostrarMensaje(mensaje, "Seleccioná un método de pago.");
        return;
    }

    if (metodoPago === "TARJETA") {
        const numero = document.getElementById("numeroTarjeta").value.trim();
        const vencimiento = document.getElementById("vencimiento").value.trim();
        const cvv = document.getElementById("cvv").value.trim();

        if (!numero || !vencimiento || !cvv) {
            mostrarMensaje(mensaje, "Completá los datos de la tarjeta.");
            return;
        }
    }

    if (!aceptaTerminos) {
        mostrarMensaje(mensaje, "Debés aceptar los términos y condiciones.");
        return;
    }

    const datos = new URLSearchParams({
        idCuartoHotel: datosReserva.cuarto.idCuartoHotel,
        fechaEntrada,
        fechaSalida,
        cantidadPersonas,
        metodoPago
    });

    try {
        const respuesta = await fetch("/reserva/confirmar", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: datos
        });

        const resultado = await respuesta.json();

        if (!respuesta.ok || !resultado.ok) {
            mostrarMensaje(mensaje, resultado.mensaje || "No se pudo registrar la reserva.");
            return;
        }

        mostrarMensaje(mensaje, resultado.mensaje, "success");
        setTimeout(() => {
            window.location.href = "/historial";
        }, 900);
    } catch (error) {
        console.error(error);
        mostrarMensaje(mensaje, "Ocurrió un error al confirmar la reserva.");
    }
}
