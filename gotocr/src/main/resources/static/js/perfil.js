document.addEventListener("DOMContentLoaded", async () => {
    await cargarPerfil();

    document.getElementById("perfilForm")?.addEventListener("submit", actualizarPerfil);
    document.getElementById("contrasenaForm")?.addEventListener("submit", actualizarContrasena);
});

async function cargarPerfil() {
    try {
        const respuesta = await fetch("/perfil/datos");

        if (respuesta.status === 401) {
            window.location.href = "/login";
            return;
        }

        if (!respuesta.ok) throw new Error("No se pudo cargar el perfil.");

        const cliente = await respuesta.json();

        document.getElementById("perfilNombreCompleto").textContent =
            `${cliente.nombre} ${cliente.apellido}`;
        document.getElementById("perfilCorreo").textContent = cliente.correo;
        document.getElementById("perfilVerificacion").textContent =
            cliente.correoVerificado ? "Correo verificado" : "Correo pendiente de verificar";

        const imagen = document.getElementById("perfilImagen");
        if (imagen && cliente.imagenPerfil) imagen.src = rutaImagen(cliente.imagenPerfil);

        document.getElementById("nombre").value = cliente.nombre;
        document.getElementById("apellido").value = cliente.apellido;
        document.getElementById("correo").value = cliente.correo;
    } catch (error) {
        console.error(error);
        mostrarMensaje(document.getElementById("perfilMensaje"), error.message);
    }
}

async function actualizarPerfil(evento) {
    evento.preventDefault();

    const mensaje = document.getElementById("perfilMensaje");
    const nombre = document.getElementById("nombre").value.trim();
    const apellido = document.getElementById("apellido").value.trim();
    const correo = document.getElementById("correo").value.trim();

    if (!nombre || !apellido || !correo) {
        mostrarMensaje(mensaje, "Nombre, apellido y correo son obligatorios.");
        return;
    }

    const datos = new URLSearchParams({ nombre, apellido, correo });

    const respuesta = await fetch("/perfil/actualizar", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: datos
    });

    const resultado = await respuesta.json();

    if (!respuesta.ok || !resultado.ok) {
        mostrarMensaje(mensaje, resultado.mensaje || "No se pudo actualizar el perfil.");
        return;
    }

    mostrarMensaje(mensaje, resultado.mensaje, "success");
    await cargarPerfil();
}

async function actualizarContrasena(evento) {
    evento.preventDefault();

    const mensaje = document.getElementById("perfilMensaje");
    const contrasenaActual = document.getElementById("contrasenaActual").value;
    const nuevaContrasena = document.getElementById("nuevaContrasena").value;
    const confirmarContrasena = document.getElementById("confirmarContrasena").value;

    if (!contrasenaActual || !nuevaContrasena || !confirmarContrasena) {
        mostrarMensaje(mensaje, "Completá los tres campos de contraseña.");
        return;
    }

    if (nuevaContrasena !== confirmarContrasena) {
        mostrarMensaje(mensaje, "Las nuevas contraseñas no coinciden.");
        return;
    }

    const datos = new URLSearchParams({
        contrasenaActual,
        nuevaContrasena,
        confirmarContrasena
    });

    const respuesta = await fetch("/perfil/contrasena", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: datos
    });

    const resultado = await respuesta.json();

    if (!respuesta.ok || !resultado.ok) {
        mostrarMensaje(mensaje, resultado.mensaje || "No se pudo actualizar la contraseña.");
        return;
    }

    mostrarMensaje(mensaje, resultado.mensaje, "success");
    document.getElementById("contrasenaForm").reset();
}
