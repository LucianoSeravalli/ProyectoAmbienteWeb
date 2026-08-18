document.addEventListener("DOMContentLoaded", cargarHotelesDestacados);

async function cargarHotelesDestacados() {
    const contenedor = document.getElementById("contenedorHotelesDestacados");

    try {
        const respuesta = await fetch("/inicio/datos/hoteles-destacados");
        if (!respuesta.ok) throw new Error("No se pudieron cargar los hoteles destacados.");

        const hoteles = await respuesta.json();

        if (!hoteles.length) {
            contenedor.innerHTML = `
                <div class="col-12">
                    <div class="alert alert-info">Todavía no hay hoteles registrados.</div>
                </div>`;
            return;
        }

        contenedor.innerHTML = hoteles.map(hotel => `
            <div class="col-lg-4 col-md-6">
                <div class="gcr-card h-100">
                    <img
                        src="${escaparHtml(rutaImagen(hotel.imagenPrincipal))}"
                        class="gcr-card-img"
                        alt="${escaparHtml(hotel.nombre)}">
                    <div class="p-4">
                        <div class="d-flex justify-content-between align-items-start mb-1">
                            <h3 class="h5 mb-0">${escaparHtml(hotel.nombre)}</h3>
                            <span class="gcr-rating">
                                <i class="bi bi-star-fill"></i>
                                ${Number(hotel.calificacionPromedio || 0).toFixed(1)}
                            </span>
                        </div>
                        <p class="text-muted small mb-3">
                            <i class="bi bi-geo-alt"></i>
                            ${escaparHtml([hotel.canton, hotel.provincia].filter(Boolean).join(", "))}
                        </p>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="small text-muted">
                                ${hotel.cuartosDisponibles ?? 0} cuartos disponibles
                            </span>
                            <a href="/hoteles/${hotel.idHotel}"
                               class="btn-gotocr btn-gotocr-outline btn-sm">
                                Ver hotel
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        `).join("");
    } catch (error) {
        console.error(error);
        contenedor.innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger">No se pudieron cargar los hoteles destacados.</div>
            </div>`;
    }
}
