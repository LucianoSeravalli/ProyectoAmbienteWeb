document.addEventListener("DOMContentLoaded", async () => {
    configurarFotoPerfil();

    await cargarPerfil();

    document.getElementById("perfilForm")
        ?.addEventListener("submit", actualizarPerfil);

    document.getElementById("contrasenaForm")
        ?.addEventListener("submit", actualizarContrasena);
});

async function cargarPerfil() {
    try {
        const respuesta = await fetch("/perfil/datos");

        if (respuesta.status === 401) {
            window.location.href = "/login";
            return;
        }

        if (!respuesta.ok) {
            throw new Error("No se pudo cargar el perfil.");
        }

        const cliente = await respuesta.json();

        document.getElementById("perfilNombreCompleto").textContent =
            `${cliente.nombre} ${cliente.apellido}`;

        document.getElementById("perfilCorreo").textContent =
            cliente.correo;

        const verificacion =
            document.getElementById("perfilVerificacion");

        if (verificacion) {
            verificacion.textContent =
                cliente.correoVerificado
                    ? "Correo verificado"
                    : "Correo pendiente de verificar";

            verificacion.classList.toggle(
                "text-bg-success",
                Boolean(cliente.correoVerificado)
            );

            verificacion.classList.toggle(
                "text-bg-warning",
                !cliente.correoVerificado
            );
        }

        const imagen =
            document.getElementById("perfilImagen");

        if (imagen) {
            imagen.src = rutaImagenPerfil(
                cliente.idCliente,
                cliente.tieneImagenPerfil
            );

            imagen.onerror = function () {
                this.onerror = null;
                this.src = "/img/perfil-default.png";
            };
        }

        document.getElementById("nombre").value =
            cliente.nombre ?? "";

        document.getElementById("apellido").value =
            cliente.apellido ?? "";

        document.getElementById("correo").value =
            cliente.correo ?? "";

    } catch (error) {
        console.error(error);
        mostrarMensaje(
            document.getElementById("perfilMensaje"),
            error.message
        );
    }
}

async function actualizarPerfil(evento) {
    evento.preventDefault();

    const mensaje =
        document.getElementById("perfilMensaje");

    const nombre =
        document.getElementById("nombre").value.trim();

    const apellido =
        document.getElementById("apellido").value.trim();

    const correo =
        document.getElementById("correo").value.trim();

    if (!nombre || !apellido || !correo) {
        mostrarMensaje(
            mensaje,
            "Nombre, apellido y correo son obligatorios."
        );
        return;
    }

    const datos =
        new URLSearchParams({
            nombre,
            apellido,
            correo
        });

    try {
        const respuesta =
            await fetch(
                "/perfil/actualizar",
                {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },
                    body: datos
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok || !resultado.ok) {
            throw new Error(
                resultado.mensaje
                || resultado.error
                || "No se pudo actualizar el perfil."
            );
        }

        mostrarMensaje(
            mensaje,
            resultado.mensaje,
            "success"
        );

        await cargarPerfil();

    } catch (error) {
        mostrarMensaje(
            mensaje,
            error.message
        );
    }
}

async function actualizarContrasena(evento) {
    evento.preventDefault();

    const mensaje =
        document.getElementById("perfilMensaje");

    const contrasenaActual =
        document.getElementById("contrasenaActual").value;

    const nuevaContrasena =
        document.getElementById("nuevaContrasena").value;

    const confirmarContrasena =
        document.getElementById("confirmarContrasena").value;

    if (
        !contrasenaActual
        || !nuevaContrasena
        || !confirmarContrasena
    ) {
        mostrarMensaje(
            mensaje,
            "Completá los tres campos de contraseña."
        );
        return;
    }

    if (nuevaContrasena !== confirmarContrasena) {
        mostrarMensaje(
            mensaje,
            "Las nuevas contraseñas no coinciden."
        );
        return;
    }

    const datos =
        new URLSearchParams({
            contrasenaActual,
            nuevaContrasena,
            confirmarContrasena
        });

    try {
        const respuesta =
            await fetch(
                "/perfil/contrasena",
                {
                    method: "POST",
                    headers: {
                        "Content-Type":
                            "application/x-www-form-urlencoded"
                    },
                    body: datos
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok || !resultado.ok) {
            throw new Error(
                resultado.mensaje
                || resultado.error
                || "No se pudo actualizar la contraseña."
            );
        }

        mostrarMensaje(
            mensaje,
            resultado.mensaje,
            "success"
        );

        document
            .getElementById("contrasenaForm")
            .reset();

    } catch (error) {
        mostrarMensaje(
            mensaje,
            error.message
        );
    }
}

// =====================================================
// FOTO DE PERFIL
// =====================================================

function configurarFotoPerfil() {
    const inputImagen =
        document.getElementById("imagenPerfil");

    const preview =
        document.getElementById("perfilImagen");

    const btnCambiarFoto =
        document.getElementById("btnCambiarFoto");

    const btnGuardarImagen =
        document.getElementById("btnGuardarImagen");

    if (btnCambiarFoto && inputImagen) {
        btnCambiarFoto.addEventListener(
            "click",
            () => inputImagen.click()
        );
    }

    if (inputImagen && preview) {
        inputImagen.addEventListener(
            "change",
            () => {
                const archivo =
                    inputImagen.files[0];

                if (!archivo) {
                    return;
                }

                const tiposPermitidos = [
                    "image/jpeg",
                    "image/png",
                    "image/webp"
                ];

                if (!tiposPermitidos.includes(archivo.type)) {
                    alert(
                        "Solo se permiten imágenes JPG, PNG o WEBP."
                    );
                    inputImagen.value = "";
                    return;
                }

                const maximo =
                    5 * 1024 * 1024;

                if (archivo.size > maximo) {
                    alert(
                        "La imagen no puede superar los 5 MB."
                    );
                    inputImagen.value = "";
                    return;
                }

                preview.src =
                    URL.createObjectURL(archivo);

                btnGuardarImagen
                    ?.classList
                    .remove("d-none");
            }
        );
    }

    if (btnGuardarImagen) {
        btnGuardarImagen.addEventListener(
            "click",
            guardarImagenPerfil
        );
    }
}

async function guardarImagenPerfil() {
    const inputImagen =
        document.getElementById("imagenPerfil");

    const preview =
        document.getElementById("perfilImagen");

    const btnGuardarImagen =
        document.getElementById("btnGuardarImagen");

    const archivo =
        inputImagen?.files[0];

    if (!archivo) {
        alert("Seleccione una imagen.");
        return;
    }

    const formData =
        new FormData();

    formData.append(
        "imagenPerfil",
        archivo
    );

    try {
        if (btnGuardarImagen) {
            btnGuardarImagen.disabled = true;
        }

        const respuesta =
            await fetch(
                "/perfil/imagen",
                {
                    method: "POST",
                    body: formData
                }
            );

        const resultado =
            await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                resultado.error
                || resultado.mensaje
                || "No fue posible guardar la imagen."
            );
        }

        alert(
            resultado.mensaje
            || "Foto de perfil actualizada correctamente."
        );

        inputImagen.value = "";

        btnGuardarImagen
            ?.classList
            .add("d-none");

        /*
         * Recargamos el perfil para obtener idCliente y
         * tieneImagenPerfil actualizados.
         */
        await cargarPerfil();

        /*
         * Evita que el navegador reutilice la imagen anterior
         * desde caché después de actualizarla.
         */
        if (preview && preview.src.includes("/perfil/imagen/")) {
            const separador =
                preview.src.includes("?") ? "&" : "?";

            preview.src =
                `${preview.src}${separador}t=${Date.now()}`;
        }

    } catch (error) {
        alert(error.message);

    } finally {
        if (btnGuardarImagen) {
            btnGuardarImagen.disabled = false;
        }
    }
}
