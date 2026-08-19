let hotelesAdmin = [];
let tiposCuartoAdmin = [];
let cuartosAdmin = [];


document.addEventListener(
        "DOMContentLoaded",
        () => {

    console.log("ADMIN JS CARGADO");

    const formHotel =
            document.getElementById("formHotel");

    const formCuarto =
            document.getElementById("formCuarto");

    const buscar = document.getElementById("buscarHotel");

    const btnNuevo =
            document.getElementById("btnNuevoHotel");

    const btnCancelar =
            document.getElementById(
                    "btnCancelarEdicion"
                    );

    const btnCancelarCuarto =
            document.getElementById(
                    "btnCancelarCuarto"
                    );

    const selectHotelCuarto =
            document.getElementById(
                    "hotelCuarto"
                    );


    formHotel?.addEventListener(
            "submit",
            guardarHotel
            );


    formCuarto?.addEventListener(
            "submit",
            guardarCuarto
            );


    buscar?.addEventListener(
            "input",
            () => {
        filtrarHoteles(
                buscar.value
                );
    }
    );


    btnNuevo?.addEventListener(
            "click",
            limpiarFormularioHotel
            );


    btnCancelar?.addEventListener(
            "click",
            limpiarFormularioHotel
            );


    btnCancelarCuarto?.addEventListener(
            "click",
            () => {
        limpiarFormularioCuarto();
    }
    );


    selectHotelCuarto?.addEventListener(
            "change",
            () => {

        const idHotel =
                Number(
                        selectHotelCuarto.value
                        );

        if (idHotel) {
            cargarCuartos(idHotel);
        } else {
            cuartosAdmin = [];
            mostrarCuartos([]);
        }
    }
    );


    cargarDashboard();
}
);


async function cargarDashboard() {

    await Promise.all([
        cargarResumen(),
        cargarHoteles(),
        cargarTiposCuarto()
    ]);
}


async function cargarResumen() {

    try {

        const respuesta =
                await fetch(
                        "/admin/resumen"
                        );

        if (!respuesta.ok) {
            throw new Error(
                    "No se pudo cargar el resumen."
                    );
        }

        const datos =
                await respuesta.json();


        document.getElementById(
                "totalHoteles"
                ).textContent =
                datos.totalHoteles ?? 0;


        document.getElementById(
                "hotelesActivos"
                ).textContent =
                datos.hotelesActivos ?? 0;


        document.getElementById(
                "cuartosDisponibles"
                ).textContent =
                datos.cuartosDisponibles ?? 0;

    } catch (error) {

        console.error(
                "Error resumen:",
                error
                );
    }
}


async function cargarHoteles() {

    const tabla =
            document.getElementById(
                    "tablaHoteles"
                    );

    try {

        const respuesta =
                await fetch(
                        "/admin/hoteles"
                        );


        if (!respuesta.ok) {

            throw new Error(
                    "No se pudieron cargar los hoteles."
                    );
        }


        hotelesAdmin =
                await respuesta.json();


        console.log(
                "Hoteles admin:",
                hotelesAdmin
                );


        mostrarHoteles(
                hotelesAdmin
                );


        llenarSelectHoteles();


    } catch (error) {

        console.error(
                "Error hoteles:",
                error
                );


        if (tabla) {

            tabla.innerHTML = `
                <tr>
                    <td
                        colspan="6"
                        class="text-center text-danger py-5">

                        ${escaparHtml(error.message)}

                    </td>
                </tr>
            `;
        }
    }
}


function mostrarHoteles(hoteles) {

    const tabla =
            document.getElementById(
                    "tablaHoteles"
                    );


    if (!tabla) {
        return;
    }


    if (!hoteles.length) {

        tabla.innerHTML = `
            <tr>
                <td
                    colspan="6"
                    class="text-center py-5">

                    No hay hoteles registrados.

                </td>
            </tr>
        `;

        return;
    }


    tabla.innerHTML =
            hoteles.map(hotel => {

                const ubicacion =
                        [
                            hotel.canton,
                            hotel.provincia
                        ]
                        .filter(Boolean)
                        .join(", ");


                const imagen =
                        hotel.tieneImagen
                        ? `/hoteles/imagen/${hotel.idHotel}`
                        : "/img/hotel-default.jpg";


                return `
                <tr>

                    <td>

                        <div class="admin-hotel-cell">

                            <img
                                class="admin-hotel-thumb"
                                src="${imagen}"
                                onerror="
                                    this.onerror=null;
                                    this.src='/img/hotel-default.jpg';
                                ">

                            <div>

                                <strong>
                                    ${escaparHtml(
                        hotel.nombre
                        )}
                                </strong>

                                <div class="admin-muted">
                                    #${hotel.idHotel}
                                </div>

                            </div>

                        </div>

                    </td>


                    <td>
                        ${escaparHtml(
                        ubicacion
                        || "Sin especificar"
                        )}
                    </td>


                    <td>

                        <i class="
                            bi
                            bi-star-fill
                            text-warning
                        "></i>

                        ${Number(
                        hotel.calificacionPromedio
                        || 0
                        ).toFixed(2)}

                    </td>


                    <td>
                        ${hotel.cuartosDisponibles ?? 0}
                    </td>


                    <td>
                        ${escaparHtml(
                        hotel.estado || ""
                        )}
                    </td>


                    <td class="text-end">

                        <button
                            type="button"
                            class="admin-icon-button"
                            title="Editar hotel"
                            onclick="
                                editarHotel(
                                    ${hotel.idHotel}
                                )
                            ">

                            <i class="bi bi-pencil"></i>

                        </button>


                        <button
                            type="button"
                            class="admin-icon-button"
                            title="Administrar cuartos"
                            onclick="
                                administrarCuartos(
                                    ${hotel.idHotel}
                                )
                            ">

                            <i class="bi bi-door-open"></i>

                        </button>


                        <button
                            type="button"
                            class="
                                admin-icon-button
                                danger
                            "
                            title="Eliminar hotel"
                            onclick="
                                eliminarHotel(
                                    ${hotel.idHotel}
                                )
                            ">

                            <i class="bi bi-trash"></i>

                        </button>

                    </td>

                </tr>
            `;
            })
            .join("");
}


function filtrarHoteles(texto) {

    const criterio =
            texto
            .trim()
            .toLowerCase();


    if (!criterio) {

        mostrarHoteles(
                hotelesAdmin
                );

        return;
    }


    const filtrados =
            hotelesAdmin.filter(hotel => {

                const datos =
                        [
                            hotel.nombre,
                            hotel.provincia,
                            hotel.canton,
                            hotel.estado
                        ]
                        .filter(Boolean)
                        .join(" ")
                        .toLowerCase();


                return datos.includes(
                        criterio
                        );
            });


    mostrarHoteles(
            filtrados
            );
}


async function guardarHotel(evento) {

    evento.preventDefault();


    const formulario =
            document.getElementById(
                    "formHotel"
                    );


    const mensaje =
            document.getElementById(
                    "mensajeAdmin"
                    );


    const datos =
            new FormData(
                    formulario
                    );


    if (!datos.get("idHotel")) {

        datos.delete(
                "idHotel"
                );
    }


    const imagen =
            document.getElementById(
                    "imagenPrincipal"
                    )?.files?.[0];


    if (imagen) {

        const permitidos = [
            "image/jpeg",
            "image/png",
            "image/webp"
        ];


        if (
                !permitidos.includes(
                        imagen.type
                        )
                ) {

            mostrarMensaje(
                    mensaje,
                    "La imagen debe ser JPG, PNG o WEBP."
                    );

            return;
        }


        if (
                imagen.size
                >
                5 * 1024 * 1024
                ) {

            mostrarMensaje(
                    mensaje,
                    "La imagen supera los 5 MB."
                    );

            return;
        }
    }


    try {

        const respuesta =
                await fetch(
                        "/admin/hoteles/guardar",
                        {
                            method: "POST",
                            body: datos
                        }
                );


        const resultado =
                await respuesta.json();


        if (!respuesta.ok) {

            throw new Error(
                    resultado.error
                    || "No se pudo guardar."
                    );
        }


        mostrarMensaje(
                mensaje,
                resultado.mensaje,
                "success"
                );


        limpiarFormularioHotel();


        await Promise.all([
            cargarHoteles(),
            cargarResumen()
        ]);


    } catch (error) {

        console.error(error);

        mostrarMensaje(
                mensaje,
                error.message
                );
    }
}


function editarHotel(idHotel) {

    const hotel =
            hotelesAdmin.find(
                    h =>
                h.idHotel === idHotel
            );


    if (!hotel) {
        return;
    }


    document.getElementById(
            "idHotel"
            ).value =
            hotel.idHotel;


    document.getElementById(
            "nombre"
            ).value =
            hotel.nombre ?? "";


    document.getElementById(
            "descripcion"
            ).value =
            hotel.descripcion ?? "";


    document.getElementById(
            "provincia"
            ).value =
            hotel.provincia ?? "";


    document.getElementById(
            "canton"
            ).value =
            hotel.canton ?? "";


    document.getElementById(
            "direccion"
            ).value =
            hotel.direccion ?? "";


    document.getElementById(
            "telefono"
            ).value =
            hotel.telefono ?? "";


    document.getElementById(
            "estado"
            ).value =
            hotel.estado ?? "ACTIVO";


    document.getElementById(
            "imagenPrincipal"
            ).value = "";


    document.getElementById(
            "tituloFormulario"
            ).textContent =
            "Editar hotel";


    document.getElementById(
            "btnNuevoHotel"
            )?.classList.remove(
            "d-none"
            );


    document.getElementById(
            "btnCancelarEdicion"
            )?.classList.remove(
            "d-none"
            );


    document.getElementById(
            "formHotel"
            ).scrollIntoView({
        behavior: "smooth"
    });
}


async function eliminarHotel(idHotel) {

    const hotel =
            hotelesAdmin.find(
                    h =>
                h.idHotel === idHotel
            );


    if (!hotel) {
        return;
    }


    if (
            !confirm(
                    `¿Eliminar el hotel "${hotel.nombre}"?`
                    )
            ) {
        return;
    }


    try {

        const respuesta =
                await fetch(
                        `/admin/hoteles/eliminar/${idHotel}`,
                        {
                            method: "POST"
                        }
                );


        const resultado =
                await respuesta.json();


        if (!respuesta.ok) {

            throw new Error(
                    resultado.error
                    || "No se pudo eliminar."
                    );
        }


        mostrarMensaje(
                document.getElementById(
                        "mensajeAdmin"
                        ),
                resultado.mensaje,
                "success"
                );


        await Promise.all([
            cargarHoteles(),
            cargarResumen()
        ]);


    } catch (error) {

        mostrarMensaje(
                document.getElementById(
                        "mensajeAdmin"
                        ),
                error.message
                );
    }
}


function limpiarFormularioHotel() {

    const formulario =
            document.getElementById(
                    "formHotel"
                    );


    formulario?.reset();


    document.getElementById(
            "idHotel"
            ).value = "";


    document.getElementById(
            "estado"
            ).value =
            "ACTIVO";


    document.getElementById(
            "tituloFormulario"
            ).textContent =
            "Registrar nuevo hotel";


    document.getElementById(
            "btnNuevoHotel"
            )?.classList.add(
            "d-none"
            );


    document.getElementById(
            "btnCancelarEdicion"
            )?.classList.add(
            "d-none"
            );
}


// ==================================================
// TIPOS CUARTO
// ==================================================

async function cargarTiposCuarto() {

    try {

        const respuesta =
                await fetch(
                        "/admin/tipos-cuarto"
                        );


        if (!respuesta.ok) {

            throw new Error(
                    "No se pudieron cargar los tipos."
                    );
        }


        tiposCuartoAdmin =
                await respuesta.json();


        const select =
                document.getElementById(
                        "tipoCuarto"
                        );


        if (!select) {
            return;
        }


        select.innerHTML = `
            <option value="">
                Seleccione un tipo
            </option>

            ${
                tiposCuartoAdmin
                .map(tipo => `
                        <option
                            value="${tipo.idTipoCuarto}">

                            ${escaparHtml(
                            tipo.nombreTipo
                            )}

                        </option>
                    `)
                .join("")
                }
        `;


    } catch (error) {

        console.error(error);
    }
}


function llenarSelectHoteles() {

    const select =
            document.getElementById(
                    "hotelCuarto"
                    );


    if (!select) {
        return;
    }


    const actual =
            select.value;


    select.innerHTML = `
        <option value="">
            Seleccione un hotel
        </option>

        ${
            hotelesAdmin
            .map(hotel => `
                    <option
                        value="${hotel.idHotel}">

                        ${escaparHtml(
                        hotel.nombre
                        )}

                    </option>
                `)
            .join("")
            }
    `;


    if (actual) {
        select.value = actual;
    }
}


// ==================================================
// CUARTOS
// ==================================================

function administrarCuartos(idHotel) {

    const select =
            document.getElementById(
                    "hotelCuarto"
                    );


    select.value =
            idHotel;


    cargarCuartos(
            idHotel
            );


    document.getElementById(
            "formCuarto"
            ).scrollIntoView({
        behavior: "smooth"
    });
}


async function cargarCuartos(idHotel) {

    try {

        const respuesta =
                await fetch(
                        `/admin/hoteles/${idHotel}/cuartos`
                        );


        if (!respuesta.ok) {

            throw new Error(
                    "No se pudieron cargar los cuartos."
                    );
        }


        cuartosAdmin =
                await respuesta.json();


        mostrarCuartos(
                cuartosAdmin
                );


    } catch (error) {

        console.error(error);
    }
}


function mostrarCuartos(cuartos) {

    const tabla =
            document.getElementById(
                    "tablaCuartos"
                    );


    if (!tabla) {
        return;
    }


    if (!cuartos.length) {

        tabla.innerHTML = `
            <tr>
                <td
                    colspan="6"
                    class="text-center py-4">

                    Este hotel no tiene cuartos.

                </td>
            </tr>
        `;

        return;
    }


    tabla.innerHTML =
            cuartos.map(cuarto => `

            <tr>

                <td>
                    #${cuarto.numeroCuarto}
                </td>

                <td>
                    ${escaparHtml(
                        cuarto.tipoCuarto || ""
                        )}
                </td>

                <td>
                    ${cuarto.cantidadPersonas}
                </td>

                <td>
                    ${formatoMoneda(
                        cuarto.precioNoche
                        )}
                </td>

                <td>
                    ${escaparHtml(
                        cuarto.estado || ""
                        )}
                </td>

                <td class="text-end">

                    <button
                        type="button"
                        class="admin-icon-button"
                        onclick="
                            editarCuarto(
                                ${cuarto.idCuartoHotel}
                            )
                        ">

                        <i class="bi bi-pencil"></i>

                    </button>


                    <button
                        type="button"
                        class="
                            admin-icon-button
                            danger
                        "
                        onclick="
                            eliminarCuarto(
                                ${cuarto.idCuartoHotel}
                            )
                        ">

                        <i class="bi bi-trash"></i>

                    </button>

                </td>

            </tr>

        `).join("");
}


async function guardarCuarto(evento) {

    evento.preventDefault();


    const formulario =
            document.getElementById(
                    "formCuarto"
                    );


    const mensaje =
            document.getElementById(
                    "mensajeCuarto"
                    );


    const datos =
            new FormData(
                    formulario
                    );


    if (!datos.get("idCuartoHotel")) {

        datos.delete(
                "idCuartoHotel"
                );
    }


    try {

        const respuesta =
                await fetch(
                        "/admin/cuartos/guardar",
                        {
                            method: "POST",
                            body: datos
                        }
                );


        const resultado =
                await respuesta.json();


        if (!respuesta.ok) {

            throw new Error(
                    resultado.error
                    || "No se pudo guardar el cuarto."
                    );
        }


        const idHotel =
                Number(
                        document.getElementById(
                                "hotelCuarto"
                                ).value
                        );


        mostrarMensaje(
                mensaje,
                resultado.mensaje,
                "success"
                );


        limpiarFormularioCuarto();


        document.getElementById(
                "hotelCuarto"
                ).value =
                idHotel;


        await cargarCuartos(
                idHotel
                );


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


function editarCuarto(
        idCuartoHotel
        ) {

    const cuarto =
            cuartosAdmin.find(
                    c =>
                c.idCuartoHotel
                        === idCuartoHotel
            );


    if (!cuarto) {
        return;
    }


    document.getElementById(
            "idCuartoHotel"
            ).value =
            cuarto.idCuartoHotel;


    document.getElementById(
            "hotelCuarto"
            ).value =
            cuarto.idHotel;


    document.getElementById(
            "tipoCuarto"
            ).value =
            cuarto.idTipoCuarto;


    document.getElementById(
            "numeroCuarto"
            ).value =
            cuarto.numeroCuarto;


    document.getElementById(
            "cantidadPersonas"
            ).value =
            cuarto.cantidadPersonas;


    document.getElementById(
            "precioNoche"
            ).value =
            cuarto.precioNoche;


    document.getElementById(
            "estadoCuarto"
            ).value =
            cuarto.estado;


    document.getElementById(
            "tituloFormularioCuarto"
            ).textContent =
            "Editar cuarto";


    document.getElementById(
            "btnCancelarCuarto"
            )?.classList.remove(
            "d-none"
            );
}


async function eliminarCuarto(
        idCuartoHotel
        ) {

    if (
            !confirm(
                    "¿Eliminar este cuarto?"
                    )
            ) {
        return;
    }


    try {

        const respuesta =
                await fetch(
                        `/admin/cuartos/eliminar/${idCuartoHotel}`,
                        {
                            method: "POST"
                        }
                );


        const resultado =
                await respuesta.json();


        if (!respuesta.ok) {

            throw new Error(
                    resultado.error
                    );
        }


        const idHotel =
                Number(
                        document.getElementById(
                                "hotelCuarto"
                                ).value
                        );


        await cargarCuartos(
                idHotel
                );


        await Promise.all([
            cargarHoteles(),
            cargarResumen()
        ]);


    } catch (error) {

        mostrarMensaje(
                document.getElementById(
                        "mensajeCuarto"
                        ),
                error.message
                );
    }
}


function limpiarFormularioCuarto() {

    const formulario =
            document.getElementById(
                    "formCuarto"
                    );


    const idHotel =
            document.getElementById(
                    "hotelCuarto"
                    ).value;


    formulario?.reset();


    document.getElementById(
            "idCuartoHotel"
            ).value = "";


    document.getElementById(
            "hotelCuarto"
            ).value =
            idHotel;


    document.getElementById(
            "estadoCuarto"
            ).value =
            "DISPONIBLE";


    document.getElementById(
            "tituloFormularioCuarto"
            ).textContent =
            "Registrar cuarto";


    document.getElementById(
            "btnCancelarCuarto"
            )?.classList.add(
            "d-none"
            );
}