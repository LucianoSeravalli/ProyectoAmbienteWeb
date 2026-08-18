let hotelesOriginales = [];

document.addEventListener("DOMContentLoaded", () => {
    cargarHoteles();

    document.getElementById("btnAplicarFiltros")?.addEventListener("click", aplicarFiltros);
    document.getElementById("ordenHoteles")?.addEventListener("change", aplicarFiltros);
    document.getElementById("filtroPrecio")?.addEventListener("input", aplicarFiltros);
});

async function cargarHoteles() {
    try {
        const respuesta = await fetch("/hoteles/datos");
        if (!respuesta.ok) throw new Error("No se pudieron obtener los hoteles.");

        hotelesOriginales = await respuesta.json();
        aplicarFiltros();
    } catch (error) {
        console.error(error);
        document.getElementById("contenedorHoteles").innerHTML = `
            <div class="alert alert-danger">No se pudieron cargar los hoteles.</div>`;
    }
}

function aplicarFiltros() {
    const provincia = document.getElementById("filtroProvincia")?.value ?? "";
    const precioMax = Number(document.getElementById("filtroPrecio")?.value ?? 500);
    const calificacion = Number(document.getElementById("filtroCalificacion")?.value || 0);
    const orden = document.getElementById("ordenHoteles")?.value ?? "recomendados";

    const tiposSeleccionados = [...document.querySelectorAll(
        "#tipoEstandar:checked, #tipoJunior:checked, #tipoSuite:checked"
    )].map(x => x.value.toLowerCase());

    let hoteles = hotelesOriginales.filter(hotel => {
        const coincideProvincia =
            !provincia || (hotel.provincia || "").toLowerCase() === provincia.toLowerCase();

        const coincideCalificacion =
            Number(hotel.calificacionPromedio || 0) >= calificacion;

        const precioMinimo = hotel.precioMinimo == null ? 0 : Number(hotel.precioMinimo);
        const coincidePrecio = precioMinimo === 0 || precioMinimo <= precioMax;

        const tipos = (hotel.tiposCuarto || []).map(t => t.toLowerCase());
        const coincideTipo =
            tiposSeleccionados.length === 0 ||
            tiposSeleccionados.some(tipo => tipos.includes(tipo));

        return coincideProvincia && coincideCalificacion && coincidePrecio && coincideTipo;
    });

    if (orden === "precioAsc") {
        hoteles.sort((a, b) => Number(a.precioMinimo || Infinity) - Number(b.precioMinimo || Infinity));
    } else if (orden === "precioDesc") {
        hoteles.sort((a, b) => Number(b.precioMinimo || 0) - Number(a.precioMinimo || 0));
    } else if (orden === "calificacion") {
        hoteles.sort((a, b) => Number(b.calificacionPromedio || 0) - Number(a.calificacionPromedio || 0));
    }

    mostrarHoteles(hoteles);
}

function mostrarHoteles(hoteles) {
    const contenedor = document.getElementById("contenedorHoteles");
    const cantidad = document.getElementById("cantidadHoteles");

    if (cantidad) cantidad.textContent = hoteles.length;

    if (!hoteles.length) {
        contenedor.innerHTML = `
            <div class="alert alert-info">
                No hay hoteles que coincidan con los filtros seleccionados.
            </div>`;
        return;
    }

    contenedor.innerHTML = hoteles.map(hotel => {
        const ubicacion = [hotel.canton, hotel.provincia].filter(Boolean).join(", ");
        const precio = hotel.precioMinimo != null
            ? `Desde <strong>${formatoMoneda(hotel.precioMinimo)}</strong>/noche`
            : `${hotel.cuartosDisponibles ?? 0} cuartos disponibles`;

        return `
            <div class="gcr-list-card mb-4">
                <div class="row g-0">
                    <div class="col-md-4">
                        <img src="${escaparHtml(rutaImagen(hotel.imagenPrincipal))}"
                             alt="${escaparHtml(hotel.nombre)}">
                    </div>
                    <div class="col-md-8 p-4 d-flex flex-column">
                        <div class="d-flex justify-content-between align-items-start">
                            <h3 class="h5 mb-1">${escaparHtml(hotel.nombre)}</h3>
                            <span class="gcr-rating">
                                <i class="bi bi-star-fill"></i>
                                ${Number(hotel.calificacionPromedio || 0).toFixed(1)}
                            </span>
                        </div>

                        <p class="text-muted small mb-2">
                            <i class="bi bi-geo-alt"></i>
                            ${escaparHtml(ubicacion)}
                        </p>

                        <p class="mb-3">${escaparHtml(hotel.descripcion || "Sin descripción disponible.")}</p>

                        <div class="mt-auto d-flex justify-content-between align-items-center">
                            <span class="gcr-price">${precio}</span>
                            <a href="/hoteles/${hotel.idHotel}"
                               class="btn-gotocr btn-gotocr-primary btn-sm">
                                Ver disponibilidad
                            </a>
                        </div>
                    </div>
                </div>
            </div>`;
    }).join("");
}
