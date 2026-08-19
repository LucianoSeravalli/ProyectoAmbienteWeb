document.addEventListener("DOMContentLoaded", () => {
    configurarMenuMovil();
    cargarSesionHeader();
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

async function cargarSesionHeader() {
    try {
        const respuesta = await fetch("/sesion/datos");

        if (!respuesta.ok) {
            return;
        }

        const sesion = await respuesta.json();

        if (!sesion.autenticado) {
            return;
        }

        mostrarUsuarioSesion(sesion);

        if (sesion.esAdmin) {
            agregarEnlaceAdministracion();
        }

    } catch (error) {
        console.error("Error al cargar la sesión:", error);
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

function mostrarUsuarioSesion(sesion) {
    const acciones = document.querySelector(".gcr-actions");

    if (!acciones) {
        return;
    }

    const foto = rutaImagenPerfil(
        sesion.idCliente,
        sesion.tieneImagenPerfil
    );

    acciones.innerHTML = `
        <a
            href="/perfil"
            class="gcr-user-session"
        >
            <span class="gcr-user-name">
                ${escaparHtml(sesion.nombre)} ${escaparHtml(sesion.apellido)}
            </span>

            <img
                src="${foto}"
                alt="Foto de perfil"
                class="gcr-user-avatar"
                onerror="
                    this.onerror=null;
                    this.src='/img/perfil-default.png';
                "
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

/*
 * Las imágenes reales ya no llegan como URLs en el JSON.
 * El navegador las solicita a endpoints que devuelven el BLOB.
 */
function rutaImagenPerfil(idCliente, tieneImagenPerfil) {
    return tieneImagenPerfil && idCliente
        ? `/perfil/imagen/${idCliente}`
        : "/img/perfil-default.png";
}

function rutaImagenHotel(idHotel, tieneImagen) {
    return tieneImagen && idHotel
        ? `/hoteles/imagen/${idHotel}`
        : "/img/hotel-default.jpg";
}

function rutaImagenCuarto(idImagen, rutaAlternativa = "/img/cuarto-default.jpg") {
    return idImagen
        ? `/imagenes-cuartos/${idImagen}`
        : rutaAlternativa;
}

/*
 * Se conserva para recursos estáticos que todavía puedan llegar como ruta.
 * No usar esta función para los BLOB de CLIENTE/HOTEL/IMAGENCUARTO.
 */
function rutaImagen(url) {
    if (!url) {
        return "/img/logo.png";
    }

    if (
        url.startsWith("http://")
        || url.startsWith("https://")
        || url.startsWith("/")
    ) {
        return url;
    }

    return `/${url}`;
}

function mostrarMensaje(elemento, mensaje, tipo = "danger") {
    if (!elemento) {
        return;
    }

    elemento.className = `alert alert-${tipo}`;
    elemento.textContent = mensaje;
    elemento.classList.remove("d-none");
}

function ocultarMensaje(elemento) {
    if (!elemento) {
        return;
    }

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
    if (!fecha) {
        return "";
    }

    const d = new Date(`${fecha}T00:00:00`);

    return new Intl.DateTimeFormat("es-CR", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    }).format(d);
}
