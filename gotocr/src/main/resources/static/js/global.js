document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.getElementById("gcrToggle");
    const nav = document.getElementById("gcrNav");

    if (toggle && nav) {
        toggle.addEventListener("click", () => {
            const abierto = nav.classList.toggle("open");
            toggle.setAttribute("aria-expanded", String(abierto));
        });
    }
});

function mostrarMensaje(elemento, mensaje, tipo = "danger") {
    if (!elemento) return;
    elemento.className = `alert alert-${tipo}`;
    elemento.textContent = mensaje;
    elemento.classList.remove("d-none");
}

function ocultarMensaje(elemento) {
    if (!elemento) return;
    elemento.classList.add("d-none");
}

function escaparHtml(valor) {
    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function formatoMoneda(valor) {
    const numero = Number(valor ?? 0);
    return new Intl.NumberFormat("es-CR", {
        style: "currency",
        currency: "USD"
    }).format(numero);
}

function formatoFecha(fecha) {
    if (!fecha) return "";
    const d = new Date(`${fecha}T00:00:00`);
    return new Intl.DateTimeFormat("es-CR", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    }).format(d);
}

function rutaImagen(url) {
    if (!url) return "/img/logo.png";
    if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) {
        return url;
    }
    return `/${url}`;
}
