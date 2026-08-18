document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("registroForm");
    const mensaje = document.getElementById("registroMensaje");
    const errorServidor = document.body.dataset.error;

    if (errorServidor && errorServidor !== "null") {
        mostrarMensaje(mensaje, errorServidor, "danger");
    }

    form?.addEventListener("submit", event => {
        ocultarMensaje(mensaje);

        const nombre = document.getElementById("nombre").value.trim();
        const apellido = document.getElementById("apellido").value.trim();
        const correo = document.getElementById("correo").value.trim();
        const contrasena = document.getElementById("contrasena").value;
        const confirmar = document.getElementById("confirmarContrasena").value;
        const terminos = document.getElementById("terminosRegistro").checked;

        if (!nombre || !apellido || !correo || !contrasena) {
            event.preventDefault();
            mostrarMensaje(mensaje, "Completá todos los campos obligatorios.");
            return;
        }

        if (!correo.includes("@")) {
            event.preventDefault();
            mostrarMensaje(mensaje, "Ingresá un correo electrónico válido.");
            return;
        }

        if (contrasena.length < 6) {
            event.preventDefault();
            mostrarMensaje(mensaje, "La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        if (contrasena !== confirmar) {
            event.preventDefault();
            mostrarMensaje(mensaje, "Las contraseñas no coinciden.");
            return;
        }

        if (!terminos) {
            event.preventDefault();
            mostrarMensaje(mensaje, "Debés aceptar los términos y condiciones.");
        }
    });
});
