let hotelesAdmin = [];

document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("formHotel");
    const buscar = document.getElementById("buscarHotel");
    const btnNuevo = document.getElementById("btnNuevoHotel");
    const btnCancelar = document.getElementById(
        "btnCancelarEdicion"
    );

    form.addEventListener("submit", guardarHotel);

    buscar.addEventListener("input", () => {
        filtrarTabla(buscar.value);
    });

    btnNuevo.addEventListener("click", limpiarFormulario);
    btnCancelar.addEventListener("click", limpiarFormulario);

    cargarDashboard();
});

async function cargarDashboard() {

    await Promise.all([
        cargarResumen(),
        cargarHoteles()
    ]);
}

async function cargarResumen() {

    try {

        const respuesta = await fetch("/admin/resumen");

        if (!respuesta.ok) {
            throw new Error(
                "No fue posible cargar el resumen"
            );
        }

        const datos = await respuesta.json();

        document.getElementById(
            "totalHoteles"
        ).textContent = datos.totalHoteles ?? 0;

        document.getElementById(
            "hotelesActivos"
        ).textContent = datos.hotelesActivos ?? 0;

        document.getElementById(
            "cuartosDisponibles"
        ).textContent = datos.cuartosDisponibles ?? 0;

    } catch (error) {

        console.error(error);
    }
}

async function cargarHoteles() {

    const tabla = document.getElementById("tablaHoteles");

    try {

        const respuesta = await fetch("/admin/hoteles");

        if (!respuesta.ok) {
            throw new Error(
                "No fue posible cargar los hoteles"
            );
        }

        hotelesAdmin = await respuesta.json();

        mostrarHoteles(hotelesAdmin);

    } catch (error) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6"
                    class="text-center text-danger py-5">
                    ${escaparHtml(error.message)}
                </td>
            </tr>
        `;
    }
}

function mostrarHoteles(hoteles) {

    const tabla = document.getElementById("tablaHoteles");

    if (!hoteles.length) {

        tabla.innerHTML = `
            <tr>
                <td colspan="6"
                    class="text-center py-5">
                    No hay hoteles registrados.
                </td>
            </tr>
        `;

        return;
    }

    tabla.innerHTML = hoteles.map(hotel => {

        const estado = String(
            hotel.estado ?? ""
        ).toUpperCase();

        const claseEstado =
            estado === "ACTIVO"
                ? "text-bg-success"
                : "text-bg-secondary";

        const ubicacion = [
            hotel.canton,
            hotel.provincia
        ]
            .filter(Boolean)
            .join(", ");

        return `
            <tr>

                <td>
                    <div class="admin-hotel-cell">

                        <img
                            class="admin-hotel-thumb"
                            src="${escaparHtml(
                                rutaImagen(
                                    hotel.imagenPrincipal
                                )
                            )}"
                            alt="${escaparHtml(
                                hotel.nombre
                            )}">

                        <div>
                            <div class="admin-hotel-name">
                                ${escaparHtml(hotel.nombre)}
                            </div>

                            <div class="admin-muted">
                                #${hotel.idHotel}
                            </div>
                        </div>

                    </div>
                </td>

                <td>
                    ${escaparHtml(
                        ubicacion || "Sin especificar"
                    )}
                </td>

                <td>
                    <i class="bi bi-star-fill text-warning"></i>
                    ${Number(
                        hotel.calificacionPromedio ?? 0
                    ).toFixed(2)}
                </td>

                <td>
                    ${hotel.cuartosDisponibles ?? 0}
                </td>

                <td>
                    <span class="badge ${claseEstado}">
                        ${escaparHtml(
                            estado || "SIN ESTADO"
                        )}
                    </span>
                </td>

                <td>
                    <div class="admin-actions">

                        <button
                            type="button"
                            class="admin-icon-button"
                            title="Editar"
                            onclick="editarHotel(
                                ${hotel.idHotel}
                            )">

                            <i class="bi bi-pencil"></i>
                        </button>

                        <button
                            type="button"
                            class="admin-icon-button danger"
                            title="Eliminar"
                            onclick="eliminarHotel(
                                ${hotel.idHotel}
                            )">

                            <i class="bi bi-trash"></i>
                        </button>

                    </div>
                </td>

            </tr>
        `;
    }).join("");
}

function filtrarTabla(texto) {

    const criterio = texto
        .trim()
        .toLowerCase();

    if (!criterio) {
        mostrarHoteles(hotelesAdmin);
        return;
    }

    const filtrados = hotelesAdmin.filter(hotel => {

        const contenido = [
            hotel.nombre,
            hotel.provincia,
            hotel.canton,
            hotel.estado
        ]
            .filter(Boolean)
            .join(" ")
            .toLowerCase();

        return contenido.includes(criterio);
    });

    mostrarHoteles(filtrados);
}

async function guardarHotel(evento) {

    evento.preventDefault();

    const mensaje = document.getElementById(
        "mensajeAdmin"
    );

    ocultarMensaje(mensaje);

    const nombre = document.getElementById(
        "nombre"
    ).value.trim();

    const estado = document.getElementById(
        "estado"
    ).value.trim();

    const calificacion = Number(
        document.getElementById(
            "calificacionPromedio"
        ).value || 0
    );

    const cuartos = Number(
        document.getElementById(
            "cuartosDisponibles"
        ).value || 0
    );

    if (!nombre) {
        mostrarMensaje(
            mensaje,
            "El nombre del hotel es obligatorio."
        );
        return;
    }

    if (!estado) {
        mostrarMensaje(
            mensaje,
            "El estado del hotel es obligatorio."
        );
        return;
    }

    if (
        calificacion < 0
        || calificacion > 5
    ) {

        mostrarMensaje(
            mensaje,
            "La calificación debe estar entre 0 y 5."
        );

        return;
    }

    if (cuartos < 0) {

        mostrarMensaje(
            mensaje,
            "Los cuartos disponibles no pueden ser negativos."
        );

        return;
    }

    const datos = new FormData(
        document.getElementById("formHotel")
    );

    // Si estamos creando, no enviamos un idHotel vacío.
    if (!datos.get("idHotel")) {
        datos.delete("idHotel");
    }

    try {

        const respuesta = await fetch(
            "/admin/hoteles/guardar",
            {
                method: "POST",
                body: datos
            }
        );

        const resultado = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.error
                || "No fue posible guardar el hotel"
            );
        }

        mostrarMensaje(
            mensaje,
            resultado.mensaje,
            "success"
        );

        limpiarFormulario();

        await Promise.all([
            cargarHoteles(),
            cargarResumen()
        ]);

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {

        mostrarMensaje(
            mensaje,
            error.message
        );
    }
}

function editarHotel(idHotel) {

    const hotel = hotelesAdmin.find(
        item => item.idHotel === idHotel
    );

    if (!hotel) return;

    document.getElementById(
        "idHotel"
    ).value = hotel.idHotel;

    document.getElementById(
        "nombre"
    ).value = hotel.nombre ?? "";

    document.getElementById(
        "descripcion"
    ).value = hotel.descripcion ?? "";

    document.getElementById(
        "imagenPrincipal"
    ).value = hotel.imagenPrincipal ?? "";

    document.getElementById(
        "provincia"
    ).value = hotel.provincia ?? "";

    document.getElementById(
        "canton"
    ).value = hotel.canton ?? "";

    document.getElementById(
        "direccion"
    ).value = hotel.direccion ?? "";

    document.getElementById(
        "telefono"
    ).value = hotel.telefono ?? "";

    document.getElementById(
        "calificacionPromedio"
    ).value =
        hotel.calificacionPromedio ?? 0;

    document.getElementById(
        "cuartosDisponibles"
    ).value =
        hotel.cuartosDisponibles ?? 0;

    document.getElementById(
        "estado"
    ).value =
        hotel.estado ?? "ACTIVO";

    document.getElementById(
        "tituloFormulario"
    ).textContent = "Editar hotel";

    document.getElementById(
        "btnNuevoHotel"
    ).classList.remove("d-none");

    document.getElementById(
        "btnCancelarEdicion"
    ).classList.remove("d-none");

    document.getElementById(
        "formHotel"
    ).scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

async function eliminarHotel(idHotel) {

    const hotel = hotelesAdmin.find(
        item => item.idHotel === idHotel
    );

    if (!hotel) return;

    const confirmar = window.confirm(
        `¿Desea eliminar el hotel "${hotel.nombre}"?`
    );

    if (!confirmar) return;

    const mensaje = document.getElementById(
        "mensajeAdmin"
    );

    try {

        const respuesta = await fetch(
            `/admin/hoteles/eliminar/${idHotel}`,
            {
                method: "POST"
            }
        );

        const resultado = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.error
                || "No fue posible eliminar el hotel"
            );
        }

        mostrarMensaje(
            mensaje,
            resultado.mensaje,
            "success"
        );

        limpiarFormulario();

        await Promise.all([
            cargarHoteles(),
            cargarResumen()
        ]);

    } catch (error) {

        mostrarMensaje(
            mensaje,
            error.message
        );
    }
}

function limpiarFormulario() {

    const form = document.getElementById(
        "formHotel"
    );

    form.reset();

    document.getElementById(
        "idHotel"
    ).value = "";

    document.getElementById(
        "calificacionPromedio"
    ).value = "0.00";

    document.getElementById(
        "cuartosDisponibles"
    ).value = "0";

    document.getElementById(
        "estado"
    ).value = "ACTIVO";

    document.getElementById(
        "tituloFormulario"
    ).textContent = "Registrar nuevo hotel";

    document.getElementById(
        "btnNuevoHotel"
    ).classList.add("d-none");

    document.getElementById(
        "btnCancelarEdicion"
    ).classList.add("d-none");
}
