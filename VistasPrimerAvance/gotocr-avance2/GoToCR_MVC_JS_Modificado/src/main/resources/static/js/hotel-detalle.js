let hotelActual = null;
let cuartosActuales = [];
let cuartoSeleccionado = null;

document.addEventListener("DOMContentLoaded", async () => {
    const idHotel = document.body.dataset.hotelId;

    if (!idHotel) {
        mostrarMensaje(document.getElementById("detalleError"), "No se recibió el hotel solicitado.");
        return;
    }

    await Promise.all([
        cargarHotel(idHotel),
        cargarCuartos(idHotel),
        cargarResenas(idHotel)
    ]);

    document.getElementById("fechaEntrada")?.addEventListener("change", actualizarResumen);
    document.getElementById("fechaSalida")?.addEventListener("change", actualizarResumen);
    document.getElementById("cantidadPersonas")?.addEventListener("change", actualizarResumen);
    document.getElementById("btnContinuarReserva")?.addEventListener("click", continuarReserva);
});

async function cargarHotel(idHotel) {
    try {
        const respuesta = await fetch(`/hoteles/datos/${idHotel}`);
        if (!respuesta.ok) throw new Error("Hotel no encontrado.");
        hotelActual = await respuesta.json();

        document.getElementById("hotelNombre").textContent = hotelActual.nombre;
        document.getElementById("hotelUbicacion").innerHTML =
            `<i class="bi bi-geo-alt"></i> ${escaparHtml(
                [hotelActual.canton, hotelActual.provincia, "Costa Rica"].filter(Boolean).join(", ")
            )}`;

        document.getElementById("hotelCalificacion").innerHTML =
            `<i class="bi bi-star-fill"></i> ${Number(hotelActual.calificacionPromedio || 0).toFixed(1)}`;

        document.getElementById("hotelDescripcion").textContent =
            hotelActual.descripcion || "Sin descripción disponible.";

        const imagen = rutaImagen(hotelActual.imagenPrincipal);
        ["hotelImagenPrincipal", "hotelImagenSecundaria1", "hotelImagenSecundaria2", "hotelImagenSecundaria3"]
            .forEach(id => {
                const img = document.getElementById(id);
                if (img) img.src = imagen;
            });
    } catch (error) {
        console.error(error);
        mostrarMensaje(document.getElementById("detalleError"), error.message);
    }
}

async function cargarCuartos(idHotel) {
    const contenedor = document.getElementById("contenedorCuartos");

    try {
        const respuesta = await fetch(`/hoteles/datos/${idHotel}/cuartos`);
        if (!respuesta.ok) throw new Error("No se pudieron cargar los cuartos.");
        cuartosActuales = await respuesta.json();

        if (!cuartosActuales.length) {
            contenedor.innerHTML = `<div class="alert alert-info">Este hotel no tiene cuartos registrados.</div>`;
            return;
        }

        contenedor.innerHTML = cuartosActuales.map(cuarto => {
            const disponible = (cuarto.estado || "").toUpperCase() === "DISPONIBLE";
            return `
                <div class="gcr-room-card mb-3 ${cuartoSeleccionado?.idCuartoHotel === cuarto.idCuartoHotel ? "border border-primary" : ""}">
                    <div class="row g-0 align-items-center">
                        <div class="col-md-3">
                            <img src="${escaparHtml(rutaImagen(cuarto.imagen || hotelActual?.imagenPrincipal))}"
                                 alt="${escaparHtml(cuarto.tipoCuarto)}">
                        </div>
                        <div class="col-md-6 p-3">
                            <span class="badge gcr-badge-tipo">${escaparHtml(cuarto.tipoCuarto)}</span>
                            <h4 class="h6 mt-2 mb-1">${escaparHtml(cuarto.tipoCuarto)}</h4>
                            <p class="small text-muted mb-0">
                                <i class="bi bi-people"></i>
                                Hasta ${cuarto.cantidadPersonas} personas · Cuarto #${cuarto.numeroCuarto}
                            </p>
                        </div>
                        <div class="col-md-3 p-3 text-md-end">
                            <p class="gcr-price mb-2">
                                ${formatoMoneda(cuarto.precioNoche)} <small>/ noche</small>
                            </p>
                            <span class="badge ${disponible ? "text-bg-success" : "text-bg-secondary"} mb-2">
                                ${escaparHtml(cuarto.estado)}
                            </span><br>
                            ${disponible
                                ? `<button type="button"
                                           class="btn-gotocr btn-gotocr-primary btn-sm"
                                           onclick="seleccionarCuarto(${cuarto.idCuartoHotel})">
                                       Seleccionar
                                   </button>`
                                : `<button class="btn-gotocr btn-gotocr-outline btn-sm" disabled>
                                       No disponible
                                   </button>`}
                        </div>
                    </div>
                </div>`;
        }).join("");

        const primeroDisponible = cuartosActuales.find(c => (c.estado || "").toUpperCase() === "DISPONIBLE");
        if (primeroDisponible) seleccionarCuarto(primeroDisponible.idCuartoHotel);
    } catch (error) {
        console.error(error);
        contenedor.innerHTML = `<div class="alert alert-danger">${escaparHtml(error.message)}</div>`;
    }
}

function seleccionarCuarto(idCuartoHotel) {
    cuartoSeleccionado = cuartosActuales.find(c => c.idCuartoHotel === idCuartoHotel) || null;
    actualizarResumen();
}

async function cargarResenas(idHotel) {
    const contenedor = document.getElementById("contenedorResenas");

    try {
        const respuesta = await fetch(`/hoteles/datos/${idHotel}/resenas`);
        if (!respuesta.ok) throw new Error("No se pudieron cargar las reseñas.");
        const resenas = await respuesta.json();

        if (!resenas.length) {
            contenedor.innerHTML = `<p class="text-muted">Este hotel todavía no tiene reseñas.</p>`;
            return;
        }

        contenedor.innerHTML = resenas.map(resena => `
            <div class="gcr-review mb-3">
                <div class="d-flex justify-content-between">
                    <strong>${escaparHtml(resena.cliente)}</strong>
                    <span class="gcr-rating">
                        <i class="bi bi-star-fill"></i> ${resena.calificacion}
                    </span>
                </div>
                <p class="text-muted small mb-1">${formatoFecha(resena.fecha)}</p>
                <p class="mb-0">${escaparHtml(resena.comentario || "Sin comentario.")}</p>
            </div>
        `).join("");
    } catch (error) {
        console.error(error);
        contenedor.innerHTML = `<div class="alert alert-danger">${escaparHtml(error.message)}</div>`;
    }
}

function actualizarResumen() {
    const entrada = document.getElementById("fechaEntrada")?.value;
    const salida = document.getElementById("fechaSalida")?.value;

    if (!cuartoSeleccionado) return;

    let noches = 0;
    if (entrada && salida) {
        noches = Math.round(
            (new Date(`${salida}T00:00:00`) - new Date(`${entrada}T00:00:00`)) / 86400000
        );
    }

    if (noches < 0) noches = 0;

    const subtotal = Number(cuartoSeleccionado.precioNoche || 0) * noches;
    const impuestos = subtotal * 0.12;
    const total = subtotal + impuestos;

    document.getElementById("detalleNoches").textContent =
        `${formatoMoneda(cuartoSeleccionado.precioNoche)} x ${noches} noches`;
    document.getElementById("subtotalEstadia").textContent = formatoMoneda(subtotal);
    document.getElementById("impuestosEstadia").textContent = formatoMoneda(impuestos);
    document.getElementById("totalEstadia").textContent = formatoMoneda(total);
}

function continuarReserva(evento) {
    evento.preventDefault();

    const entrada = document.getElementById("fechaEntrada")?.value;
    const salida = document.getElementById("fechaSalida")?.value;
    const personas = Number(document.getElementById("cantidadPersonas")?.value || 0);

    if (!cuartoSeleccionado) {
        mostrarMensaje(document.getElementById("detalleError"), "Seleccioná un cuarto disponible.");
        return;
    }

    if (!entrada || !salida) {
        mostrarMensaje(document.getElementById("detalleError"), "Seleccioná la fecha de entrada y salida.");
        return;
    }

    if (new Date(salida) <= new Date(entrada)) {
        mostrarMensaje(document.getElementById("detalleError"), "La fecha de salida debe ser posterior a la entrada.");
        return;
    }

    if (personas > cuartoSeleccionado.cantidadPersonas) {
        mostrarMensaje(
            document.getElementById("detalleError"),
            `Este cuarto admite como máximo ${cuartoSeleccionado.cantidadPersonas} personas.`
        );
        return;
    }

    const parametros = new URLSearchParams({
        fechaEntrada: entrada,
        fechaSalida: salida,
        cantidadPersonas: personas
    });

    window.location.href = `/reserva/${cuartoSeleccionado.idCuartoHotel}?${parametros}`;
}
