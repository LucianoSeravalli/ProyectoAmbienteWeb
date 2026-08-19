let hotelActual = null;
let cuartosActuales = [];
let cuartoSeleccionado = null;


document.addEventListener("DOMContentLoaded", async () => {

    const idHotel =
            document.body.dataset.hotelId;


    if (!idHotel) {

        mostrarMensaje(
                document.getElementById("detalleError"),
                "No se recibió el hotel solicitado."
                );

        return;
    }


    try {

        /*
         * Primero cargamos el hotel.
         * Después los cuartos.
         *
         * Así hotelActual ya existe cuando
         * construimos las imágenes de cuartos.
         */
        await cargarHotel(idHotel);

        await Promise.all([
            cargarCuartos(idHotel),
            cargarResenas(idHotel)
        ]);


        document
                .getElementById("fechaEntrada")
                ?.addEventListener(
                        "change",
                        actualizarResumen
                        );


        document
                .getElementById("fechaSalida")
                ?.addEventListener(
                        "change",
                        actualizarResumen
                        );


        document
                .getElementById("cantidadPersonas")
                ?.addEventListener(
                        "change",
                        actualizarResumen
                        );


        document
                .getElementById("btnContinuarReserva")
                ?.addEventListener(
                        "click",
                        continuarReserva
                        );


    } catch (error) {

        console.error(
                "ERROR INICIANDO DETALLE:",
                error
                );


        document
                .getElementById("formResena")
                ?.addEventListener(
                        "submit",
                        guardarResena
                        );
    }
});


// =====================================================
// HOTEL
// =====================================================

async function cargarHotel(idHotel) {

    try {

        const respuesta =
                await fetch(
                        `/hoteles/datos/${idHotel}`
                        );


        if (!respuesta.ok) {

            throw new Error(
                    "Hotel no encontrado."
                    );
        }


        hotelActual =
                await respuesta.json();


        console.log(
                "HOTEL:",
                hotelActual
                );


        document.getElementById(
                "hotelNombre"
                ).textContent =
                hotelActual.nombre
                ?? "Hotel";


        const ubicacion =
                [
                    hotelActual.canton,
                    hotelActual.provincia,
                    "Costa Rica"
                ]
                .filter(Boolean)
                .join(", ");


        document.getElementById(
                "hotelUbicacion"
                ).innerHTML = `

            <i class="bi bi-geo-alt"></i>

            ${escaparHtml(ubicacion)}

        `;


        document.getElementById(
                "hotelCalificacion"
                ).innerHTML = `

            <i class="bi bi-star-fill"></i>

            ${Number(
                hotelActual.calificacionPromedio
                || 0
                ).toFixed(1)}

        `;


        document.getElementById(
                "hotelDescripcion"
                ).textContent =
                hotelActual.descripcion
                || "Sin descripción disponible.";


        const imagen =
                hotelActual.tieneImagen
                ? `/hoteles/imagen/${hotelActual.idHotel}`
                : "/img/hotel-default.jpg";


        [
            "hotelImagenPrincipal",
            "hotelImagenSecundaria1",
            "hotelImagenSecundaria2",
            "hotelImagenSecundaria3"
        ]
                .forEach(id => {

                    const img =
                            document.getElementById(id);


                    if (!img) {
                        return;
                    }


                    img.src =
                            imagen;


                    img.onerror =
                            function () {

                                this.onerror = null;

                                this.src =
                                        "/img/hotel-default.jpg";
                            };
                });


    } catch (error) {

        console.error(
                "ERROR CARGANDO HOTEL:",
                error
                );


        mostrarMensaje(
                document.getElementById(
                        "detalleError"
                        ),
                error.message
                );


        throw error;
    }
}


// =====================================================
// CUARTOS
// =====================================================

async function cargarCuartos(idHotel) {

    const contenedor =
            document.getElementById(
                    "contenedorCuartos"
                    );


    if (!contenedor) {
        return;
    }


    try {

        console.log(
                "Cargando cuartos del hotel:",
                idHotel
                );


        const respuesta =
                await fetch(
                        `/hoteles/datos/${idHotel}/cuartos`
                        );


        console.log(
                "Status cuartos:",
                respuesta.status
                );


        if (!respuesta.ok) {

            throw new Error(
                    "No se pudieron cargar los cuartos."
                    );
        }


        cuartosActuales =
                await respuesta.json();


        console.log(
                "CUARTOS RECIBIDOS:",
                cuartosActuales
                );


        if (!cuartosActuales.length) {

            contenedor.innerHTML = `

                <div class="alert alert-info">

                    Este hotel no tiene cuartos registrados.

                </div>

            `;

            return;
        }


        contenedor.innerHTML =
                cuartosActuales
                .map(cuarto => {

                    const disponible =
                            String(
                                    cuarto.estado
                                    || ""
                                    ).toUpperCase()
                            ===
                            "DISPONIBLE";


                    /*
                     * Imagen propia del cuarto.
                     *
                     * Si no tiene imagen propia,
                     * mostramos la del hotel.
                     */
                    const imagenCuarto =
                            cuarto.tieneImagen
                            &&
                            cuarto.idImagen

                            ? `/imagenes-cuartos/${cuarto.idImagen}`

                            : (
                                    hotelActual
                                    &&
                                    hotelActual.tieneImagen

                                    ? `/hoteles/imagen/${hotelActual.idHotel}`

                                    : "/img/cuarto-default.jpg"
                                    );


                    const seleccionado =
                            cuartoSeleccionado
                            &&
                            cuartoSeleccionado.idCuartoHotel
                            ===
                            cuarto.idCuartoHotel;


                    return `

                        <div
                            class="
                                gcr-room-card
                                mb-3
                                ${
                            seleccionado
                            ? "border border-primary"
                            : ""
                            }
                            "
                            id="cuarto-${cuarto.idCuartoHotel}">

                            <div
                                class="
                                    row
                                    g-0
                                    align-items-center
                                ">


                                <div class="col-md-3">

                                    <img
                                        src="${imagenCuarto}"
                                        alt="${escaparHtml(
                                    cuarto.tipoCuarto
                                    || "Cuarto"
                                    )}"
                                        class="w-100"
                                        style="
                                            height: 180px;
                                            object-fit: cover;
                                        "
                                        onerror="
                                            this.onerror=null;
                                            this.src='/img/cuarto-default.jpg';
                                        ">

                                </div>


                                <div class="col-md-6 p-3">

                                    <span
                                        class="
                                            badge
                                            gcr-badge-tipo
                                        ">

                                        ${escaparHtml(
                            cuarto.tipoCuarto
                            || "Habitación"
                            )}

                                    </span>


                                    <h4
                                        class="
                                            h6
                                            mt-2
                                            mb-1
                                        ">

                                        ${
                            escaparHtml(
                                    cuarto.tipoCuarto
                                    || "Habitación"
                                    )
                            }

                                    </h4>


                                    <p
                                        class="
                                            small
                                            text-muted
                                            mb-0
                                        ">

                                        <i
                                            class="
                                                bi
                                                bi-people
                                            ">
                                        </i>

                                        Hasta
                                        ${cuarto.cantidadPersonas}
                                        personas

                                        · Cuarto
                                        #${cuarto.numeroCuarto}

                                    </p>

                                </div>


                                <div
                                    class="
                                        col-md-3
                                        p-3
                                        text-md-end
                                    ">

                                    <p
                                        class="
                                            gcr-price
                                            mb-2
                                        ">

                                        ${
                            formatoMoneda(
                                    cuarto.precioNoche
                                    )
                            }

                                        <small>
                                            / noche
                                        </small>

                                    </p>


                                    <span
                                        class="
                                            badge
                                            ${
                            disponible
                            ? "text-bg-success"
                            : "text-bg-secondary"
                            }
                                            mb-2
                                        ">

                                        ${escaparHtml(
                                    cuarto.estado
                                    || "SIN ESTADO"
                                    )}

                                    </span>


                                    <br>


                                    ${
                            disponible

                            ? `

                                                <button
                                                    type="button"
                                                    class="
                                                        btn-gotocr
                                                        btn-gotocr-primary
                                                        btn-sm
                                                    "
                                                    onclick="
                                                        seleccionarCuarto(
                                                            ${cuarto.idCuartoHotel}
                                                        )
                                                    ">

                                                    ${
                            seleccionado
                            ? "Seleccionado"
                            : "Seleccionar cuarto"
                            }

                                                </button>

                                            `

                            : `

                                                <button
                                                    type="button"
                                                    class="
                                                        btn-gotocr
                                                        btn-gotocr-outline
                                                        btn-sm
                                                    "
                                                    disabled>

                                                    No disponible

                                                </button>

                                            `
                            }

                                </div>

                            </div>

                        </div>

                    `;
                })
                .join("");


    } catch (error) {

        console.error(
                "ERROR CARGANDO CUARTOS:",
                error
                );


        contenedor.innerHTML = `

            <div class="alert alert-danger">

                ${escaparHtml(
                error.message
                )}

            </div>

        `;
    }
}


// =====================================================
// SELECCIONAR CUARTO
// =====================================================

function seleccionarCuarto(
        idCuartoHotel
        ) {

    cuartoSeleccionado =
            cuartosActuales.find(
                    cuarto =>
                cuarto.idCuartoHotel
                        ===
                        idCuartoHotel
            )
            || null;


    console.log(
            "CUARTO SELECCIONADO:",
            cuartoSeleccionado
            );


    /*
     * Volvemos a pintar los cuartos
     * para mostrar cuál quedó seleccionado.
     */
    if (hotelActual) {

        mostrarCuartosLocales();
    }


    actualizarResumen();
}


function mostrarCuartosLocales() {

    const contenedor =
            document.getElementById(
                    "contenedorCuartos"
                    );


    if (!contenedor) {
        return;
    }


    contenedor.innerHTML =
            cuartosActuales.map(
                    cuarto => {

                        const disponible =
                                String(
                                        cuarto.estado || ""
                                        ).toUpperCase()
                                ===
                                "DISPONIBLE";


                        const seleccionado =
                                cuartoSeleccionado
                                &&
                                cuartoSeleccionado.idCuartoHotel
                                ===
                                cuarto.idCuartoHotel;


                        const imagenCuarto =
                                cuarto.tieneImagen
                                &&
                                cuarto.idImagen

                                ? `/imagenes-cuartos/${cuarto.idImagen}`

                                : (
                                        hotelActual
                                        &&
                                        hotelActual.tieneImagen

                                        ? `/hoteles/imagen/${hotelActual.idHotel}`

                                        : "/img/cuarto-default.jpg"
                                        );


                        return `

                    <div
                        class="
                            gcr-room-card
                            mb-3
                            ${
                                seleccionado
                                ? "border border-primary"
                                : ""
                                }
                        ">

                        <div
                            class="
                                row
                                g-0
                                align-items-center
                            ">


                            <div class="col-md-3">

                                <img
                                    src="${imagenCuarto}"
                                    class="w-100"
                                    style="
                                        height:180px;
                                        object-fit:cover;
                                    "
                                    onerror="
                                        this.onerror=null;
                                        this.src='/img/cuarto-default.jpg';
                                    ">

                            </div>


                            <div class="col-md-6 p-3">

                                <h4 class="h6">

                                    ${escaparHtml(
                                        cuarto.tipoCuarto
                                        || "Habitación"
                                        )}

                                </h4>


                                <p
                                    class="
                                        small
                                        text-muted
                                    ">

                                    <i
                                        class="
                                            bi
                                            bi-people
                                        ">
                                    </i>

                                    Hasta
                                    ${cuarto.cantidadPersonas}
                                    personas

                                    · Cuarto
                                    #${cuarto.numeroCuarto}

                                </p>

                            </div>


                            <div
                                class="
                                    col-md-3
                                    p-3
                                    text-md-end
                                ">

                                <p class="gcr-price">

                                    ${
                                formatoMoneda(
                                        cuarto.precioNoche
                                        )
                                }

                                    <small>
                                        / noche
                                    </small>

                                </p>


                                ${
                                disponible

                                ? `

                                            <button
                                                type="button"
                                                class="
                                                    btn-gotocr
                                                    btn-gotocr-primary
                                                    btn-sm
                                                "
                                                onclick="
                                                    seleccionarCuarto(
                                                        ${cuarto.idCuartoHotel}
                                                    )
                                                ">

                                                ${
                                seleccionado
                                ? "Seleccionado"
                                : "Seleccionar cuarto"
                                }

                                            </button>

                                        `

                                : `

                                            <button
                                                type="button"
                                                class="
                                                    btn-gotocr
                                                    btn-gotocr-outline
                                                    btn-sm
                                                "
                                                disabled>

                                                No disponible

                                            </button>

                                        `
                                }

                            </div>

                        </div>

                    </div>

                `;
                    }
            )
            .join("");
}


// =====================================================
// RESEÑAS
// =====================================================

async function cargarResenas(idHotel) {

    const contenedor =
            document.getElementById(
                    "contenedorResenas"
                    );


    if (!contenedor) {
        return;
    }


    try {

        const respuesta =
                await fetch(
                        `/hoteles/datos/${idHotel}/resenas`
                        );


        if (!respuesta.ok) {

            throw new Error(
                    "No se pudieron cargar las reseñas."
                    );
        }


        const resenas =
                await respuesta.json();


        if (!resenas.length) {

            contenedor.innerHTML = `

                <p class="text-muted">

                    Este hotel todavía no tiene reseñas.

                </p>

            `;

            return;
        }


        contenedor.innerHTML =
                resenas.map(
                        resena => `

                    <div class="gcr-review mb-3">

                        <div
                            class="
                                d-flex
                                justify-content-between
                            ">

                            <strong>

                                ${escaparHtml(
                                    resena.cliente
                                    || "Cliente"
                                    )}

                            </strong>


                            <span class="gcr-rating">

                                <i
                                    class="
                                        bi
                                        bi-star-fill
                                    ">
                                </i>

                                ${resena.calificacion}

                            </span>

                        </div>


                        <p
                            class="
                                text-muted
                                small
                                mb-1
                            ">

                            ${formatoFecha(
                            resena.fecha
                            )}

                        </p>


                        <p class="mb-0">

                            ${escaparHtml(
                            resena.comentario
                            || "Sin comentario."
                            )}

                        </p>

                    </div>

                `
                )
                .join("");


    } catch (error) {

        console.error(
                error
                );


        contenedor.innerHTML = `

            <div class="alert alert-danger">

                ${escaparHtml(
                error.message
                )}

            </div>

        `;
    }
}


// =====================================================
// RESUMEN
// =====================================================

function actualizarResumen() {

    const entrada =
            document.getElementById(
                    "fechaEntrada"
                    )?.value;


    const salida =
            document.getElementById(
                    "fechaSalida"
                    )?.value;


    if (!cuartoSeleccionado) {

        document.getElementById(
                "detalleNoches"
                ).textContent =
                "Seleccione un cuarto";


        document.getElementById(
                "subtotalEstadia"
                ).textContent =
                formatoMoneda(0);


        document.getElementById(
                "impuestosEstadia"
                ).textContent =
                formatoMoneda(0);


        document.getElementById(
                "totalEstadia"
                ).textContent =
                formatoMoneda(0);


        return;
    }


    let noches = 0;


    if (entrada && salida) {

        noches =
                Math.round(
                        (
                                new Date(
                                        `${salida}T00:00:00`
                                        )
                                -
                                new Date(
                                        `${entrada}T00:00:00`
                                        )
                                )
                        /
                86400000
                        );
    }


    if (noches < 0) {
        noches = 0;
    }


    const precio =
            Number(
                    cuartoSeleccionado.precioNoche
                    || 0
                    );


    const subtotal =
            precio * noches;


    const impuestos =
            subtotal * 0.12;


    const total =
            subtotal + impuestos;


    document.getElementById(
            "detalleNoches"
            ).textContent =
            `${formatoMoneda(precio)} x ${noches} noches`;


    document.getElementById(
            "subtotalEstadia"
            ).textContent =
            formatoMoneda(
                    subtotal
                    );


    document.getElementById(
            "impuestosEstadia"
            ).textContent =
            formatoMoneda(
                    impuestos
                    );


    document.getElementById(
            "totalEstadia"
            ).textContent =
            formatoMoneda(
                    total
                    );
}


// =====================================================
// CONTINUAR A RESERVA
// =====================================================

function continuarReserva(
        evento
        ) {

    evento.preventDefault();


    const error =
            document.getElementById(
                    "detalleError"
                    );


    if (!cuartoSeleccionado) {

        mostrarMensaje(
                error,
                "Seleccioná un cuarto disponible."
                );

        return;
    }


    const entrada =
            document.getElementById(
                    "fechaEntrada"
                    )?.value;


    const salida =
            document.getElementById(
                    "fechaSalida"
                    )?.value;


    const personas =
            Number(
                    document.getElementById(
                            "cantidadPersonas"
                            )?.value
                    || 1
                    );


    if (!entrada || !salida) {

        mostrarMensaje(
                error,
                "Seleccioná la fecha de entrada y salida."
                );

        return;
    }


    if (
            new Date(salida)
            <=
            new Date(entrada)
            ) {

        mostrarMensaje(
                error,
                "La fecha de salida debe ser posterior a la entrada."
                );

        return;
    }


    if (
            personas
            >
            Number(
                    cuartoSeleccionado.cantidadPersonas
                    )
            ) {

        mostrarMensaje(
                error,
                `Este cuarto admite como máximo ${cuartoSeleccionado.cantidadPersonas} personas.`
                );

        return;
    }


    const parametros =
            new URLSearchParams({
                fechaEntrada:
                        entrada,

                fechaSalida:
                        salida,

                cantidadPersonas:
                        personas
            });


    window.location.href =
            `/reserva/${cuartoSeleccionado.idCuartoHotel}?${parametros.toString()}`;
}


async function guardarResena(
    evento
) {

    evento.preventDefault();


    const idHotel =
        document.body.dataset.hotelId;


    const mensaje =
        document.getElementById(
            "mensajeResena"
        );


    const calificacion =
        document.getElementById(
            "calificacionResena"
        )?.value;


    const comentario =
        document.getElementById(
            "comentarioResena"
        )?.value
        ?.trim()
        || "";


    if (!calificacion) {

        mostrarMensaje(
            mensaje,
            "Seleccione una calificación."
        );

        return;
    }


    const datos =
        new URLSearchParams();


    datos.append(
        "calificacion",
        calificacion
    );


    datos.append(
        "comentario",
        comentario
    );


    try {

        const respuesta =
            await fetch(
                `/hoteles/${idHotel}/resenas/guardar`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },

                    body:
                        datos.toString()
                }
            );


        if (respuesta.status === 401) {

            window.location.href =
                "/login";

            return;
        }


        const resultado =
            await respuesta.json();


        if (!respuesta.ok) {

            throw new Error(
                resultado.error
                || "No fue posible guardar la reseña."
            );
        }


        mostrarMensaje(
            mensaje,
            resultado.mensaje,
            "success"
        );


        document
            .getElementById(
                "formResena"
            )
            ?.reset();


        /*
         * Recargamos:
         *
         * 1. hotel -> porque cambió promedio
         * 2. reseñas -> porque agregamos/modificamos
         */
        await Promise.all([
            cargarHotel(idHotel),
            cargarResenas(idHotel)
        ]);


    } catch (error) {

        console.error(
            "ERROR GUARDANDO RESEÑA:",
            error
        );


        mostrarMensaje(
            mensaje,
            error.message
        );
    }
}


async function cargarResenas(
    idHotel
) {

    const contenedor =
        document.getElementById(
            "contenedorResenas"
        );


    if (!contenedor) {
        return;
    }


    try {

        const respuesta =
            await fetch(
                `/hoteles/datos/${idHotel}/resenas`
            );


        if (!respuesta.ok) {

            throw new Error(
                "No se pudieron cargar las reseñas."
            );
        }


        const resenas =
            await respuesta.json();


        if (!resenas.length) {

            contenedor.innerHTML = `

                <p class="text-muted">

                    Este hotel todavía no tiene reseñas.

                </p>

            `;

            return;
        }


        contenedor.innerHTML =
            resenas.map(
                resena => {

                    const estrellas =
                        generarEstrellas(
                            Number(
                                resena.calificacion
                                || 0
                            )
                        );


                    return `

                        <div class="gcr-review mb-3">

                            <div
                                class="
                                    d-flex
                                    justify-content-between
                                    align-items-start
                                ">

                                <div>

                                    <strong>

                                        ${escaparHtml(
                                            resena.cliente
                                            || "Cliente"
                                        )}

                                    </strong>


                                    <div
                                        class="
                                            gcr-rating
                                            mt-1
                                        ">

                                        ${estrellas}

                                    </div>

                                </div>


                                <span
                                    class="
                                        small
                                        text-muted
                                    ">

                                    ${
                                        formatoFecha(
                                            resena.fecha
                                        )
                                    }

                                </span>

                            </div>


                            <p class="mb-0 mt-2">

                                ${escaparHtml(
                                    resena.comentario
                                    || "Sin comentario."
                                )}

                            </p>

                        </div>

                    `;
                }
            )
            .join("");


    } catch (error) {

        console.error(
            "ERROR RESEÑAS:",
            error
        );


        contenedor.innerHTML = `

            <div class="alert alert-danger">

                ${escaparHtml(
                    error.message
                )}

            </div>

        `;
    }
}