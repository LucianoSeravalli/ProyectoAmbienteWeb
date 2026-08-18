document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    const mensaje = document.getElementById("loginMensaje");
    const errorServidor = document.body.dataset.error;
    const params = new URLSearchParams(window.location.search);

    if (errorServidor && errorServidor !== "null") {
        mostrarMensaje(mensaje, errorServidor, "danger");
    } else if (params.has("registroExitoso")) {
        mostrarMensaje(mensaje, "Cuenta creada correctamente. Ya podés iniciar sesión.", "success");
    }

    form?.addEventListener("submit", event => {
        ocultarMensaje(mensaje);

        const correo = document.getElementById("correo").value.trim();
        const contrasena = document.getElementById("contrasena").value;

        if (!correo) {
            event.preventDefault();
            mostrarMensaje(mensaje, "El correo electrónico es obligatorio.");
            return;
        }

        if (!correo.includes("@")) {
            event.preventDefault();
            mostrarMensaje(mensaje, "Ingresá un correo electrónico válido.");
            return;
        }

        if (!contrasena.trim()) {
            event.preventDefault();
            mostrarMensaje(mensaje, "La contraseña es obligatoria.");
        }
    });
});
