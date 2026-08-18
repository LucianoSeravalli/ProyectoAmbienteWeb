document.addEventListener("DOMContentLoaded", () => {
    configurarMenuMovil();
    cargarEstadoSesion();
});

function configurarMenuMovil() {
    const toggle = document.getElementById("gcrToggle");
    const nav = document.getElementById("gcrNav");

    if (toggle && nav) {
        toggle.addEventListener("click", () => {
            const abierto = nav.classList.toggle("open");
            toggle.setAttribute("aria-expanded", String(abierto));
        });
    }
}

async function cargarEstadoSesion() {
    try {
        const respuesta = await fetch("/sesion/datos");

        if (!respuesta.ok) return;

        const sesion = await respuesta.json();

        if (sesion.esAdmin) {
            agregarEnlaceAdministracion();
        }
    } catch (error) {
        console.error("No fue posible consultar la sesión:", error);
    }
}

function agregarEnlaceAdministracion() {
    const nav = document.getElementById("gcrNav");

    if (!nav || document.getElementById("enlaceAdministracion")) {
        return;
    }

    const enlace = document.createElement("a");

    enlace.id = "enlaceAdministracion";
    enlace.href = "/admin";
    enlace.textContent = "Administración";

    if (window.location.pathname.startsWith("/admin")) {
        enlace.classList.add("active");
    }

    nav.appendChild(enlace);
}

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

    if (
        url.startsWith("http://")
        || url.startsWith("https://")
        || url.startsWith("/")
    ) {
        return url;
    }

    return `/${url}`;
}

document.addEventListener("DOMContentLoaded", () => {
    cargarSesionHeader();
});

async function cargarSesionHeader() {

    try {

        const respuesta = await fetch("/sesion/datos");

        if (!respuesta.ok) {
            return;
        }

        const sesion = await respuesta.json();

        if (sesion.autenticado) {
            mostrarUsuarioSesion(sesion);

            if (sesion.esAdmin) {
                agregarEnlaceAdministracion();
            }
        }

    } catch (error) {
        console.error("Error al cargar sesión:", error);
    }
}


function mostrarUsuarioSesion(sesion) {

    const contenedorAcciones =
        document.querySelector(".gcr-actions");

    if (!contenedorAcciones) {
        return;
    }

    const imagen =
        sesion.imagenPerfil && sesion.imagenPerfil.trim() !== ""
            ? sesion.imagenPerfil
            : "/img/perfil-default.png";

    contenedorAcciones.innerHTML = `
        <a
            href="/perfil"
            class="gcr-user-session"
            title="Ir a mi perfil"
        >
            <span class="gcr-user-name">
                ${sesion.nombre} ${sesion.apellido}
            </span>

            <img
                src="${imagen}"
                alt="Foto de perfil de ${sesion.nombre}"
                class="gcr-user-avatar"
            >
        </a>

        <a
            href="/logout"
            class="btn-gotocr btn-gotocr-outline"
        >
            Cerrar sesión
        </a>
    `;
}

