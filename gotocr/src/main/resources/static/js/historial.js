let reservasCliente = [];

document.addEventListener("DOMContentLoaded", async () => {
    await cargarHistorial();

    document.querySelectorAll(".gcr-tabs button").forEach(boton => {
        boton.addEventListener("click", () => {
            document.querySelectorAll(".gcr-tabs button")
                .forEach(b => b.classList.remove("active"));

            boton.classList.add("active");
            mostrarReservasPorEstado(boton.dataset.estado);
        });
    });
});

async function cargarHistorial() {
    const contenedor = document.getElementById("contenedorReservas");

    try {
        const respuesta = await fetch("/historial/datos");

        if (respuesta.status === 401) {
            window.location.href = "/login";
            return;
        }

        if (!respuesta.ok) throw new Error("No se pudo cargar el historial.");

        const datos = await respuesta.json();

        document.getElementById("historialNombreCliente").textContent =
            `${datos.cliente.nombre} ${datos.cliente.apellido}`;
        document.getElementById("historialCorreoCliente").textContent =
            datos.cliente.correo;

        const imagenPerfil =
            document.getElementById("historialImagenPerfil");

        if (imagenPerfil) {
            imagenPerfil.src = rutaImagenPerfil(
                datos.cliente.idCliente,
                datos.cliente.tieneImagenPerfil
            );

            imagenPerfil.onerror = function () {
                this.onerror = null;
                this.src = "/img/perfil-default.png";
            };
        }

        reservasCliente = datos.reservas || [];
        actualizarContadores();
        mostrarReservasPorEstado("TODAS");
    } catch (error) {
        console.error(error);
        contenedor.innerHTML = `<div class="alert alert-danger">${escaparHtml(error.message)}</div>`;
    }
}

function actualizarContadores() {
    const botones = [...document.querySelectorAll(".gcr-tabs button")];

    const contadores = {
        TODAS: reservasCliente.length,
        CONFIRMADA: reservasCliente.filter(r => r.estadoReserva === "CONFIRMADA").length,
        COMPLETADA: reservasCliente.filter(r => r.estadoReserva === "COMPLETADA").length,
        CANCELADA: reservasCliente.filter(r => r.estadoReserva === "CANCELADA").length
    };

    const nombres = {
        TODAS: "Todas",
        CONFIRMADA: "Próximas",
        COMPLETADA: "Completadas",
        CANCELADA: "Canceladas"
    };

    botones.forEach(boton => {
        const estado = boton.dataset.estado;
        boton.textContent = `${nombres[estado]} (${contadores[estado] || 0})`;
    });
}

function mostrarReservasPorEstado(estado) {
    const reservas = estado === "TODAS"
        ? reservasCliente
        : reservasCliente.filter(r => r.estadoReserva === estado);

    const contenedor = document.getElementById("contenedorReservas");

    if (!reservas.length) {
        contenedor.innerHTML = `<div class="alert alert-info">No hay reservas en esta categoría.</div>`;
        return;
    }

    contenedor.innerHTML = reservas.map(reserva => {
        const estado = (reserva.estadoReserva || "").toUpperCase();

        const imagenHotel = rutaImagenHotel(
            reserva.idHotel,
            reserva.tieneImagenHotel
        );
        const clase = estado === "CONFIRMADA"
            ? "text-bg-warning"
            : estado === "COMPLETADA"
                ? "text-bg-success"
                : estado === "CANCELADA"
                    ? "text-bg-danger"
                    : "text-bg-secondary";

        return `
            <div class="gcr-reservation-card mb-3 ${estado === "CANCELADA" ? "is-cancelled" : ""}">
                <img src="${imagenHotel}"
                     alt="${escaparHtml(reserva.hotel)}"
                     onerror="this.onerror=null; this.src='/img/hotel-default.jpg';">

                <div class="flex-grow-1">
                    <div class="d-flex justify-content-between flex-wrap gap-2">
                        <h3 class="h6 mb-1">${escaparHtml(reserva.hotel)}</h3>
                        <span class="badge ${clase}">${escaparHtml(reserva.estadoReserva)}</span>
                    </div>

                    <p class="small text-muted mb-1">
                        <i class="bi bi-door-closed"></i>
                        ${escaparHtml(reserva.tipoCuarto)} · ${reserva.cantidadPersonas} personas
                    </p>

                    <p class="small text-muted mb-0">
                        <i class="bi bi-calendar-event"></i>
                        ${formatoFecha(reserva.fechaEntrada)} — ${formatoFecha(reserva.fechaSalida)}
                        · Reserva #GCR-${String(reserva.idReserva).padStart(4, "0")}
                    </p>
                </div>

                <div class="text-md-end">
                    <p class="gcr-price mb-2">${formatoMoneda(reserva.precioTotal)}</p>
                    <a href="/hoteles/${reserva.idHotel}"
                       class="btn-gotocr btn-gotocr-outline btn-sm">
                        Ver hotel
                    </a>
                </div>
            </div>`;
    }).join("");
}
