document.addEventListener("DOMContentLoaded", function() {
    const busquedaGlobal = document.getElementById("busquedaGlobal");
    const filtroEstado = document.getElementById("filtroEstado");
    const filtroPrioridad = document.getElementById("filtroPrioridad");
    const filtroFechaDesde = document.getElementById("filtroFechaDesde");
    const filtroFechaHasta = document.getElementById("filtroFechaHasta");

    const filas = Array.from(document.querySelectorAll(".fila-incidencia"));
    const contadorFilas = document.getElementById("contadorFilas");
    const contadorExcel = document.getElementById("contadorExcel");

    // El paginador solo pagina entre las filas que actualmente cumplen el filtro
    // (marcadas con data-coincide="1"), dejando siempre ocultas las que no coinciden.
    const paginadorReportes = crearPaginador({
        obtenerItems: () => filas.filter(f => f.dataset.coincide === "1"),
        contenedorNavId: "paginacion-reportes-container",
        ulId: "paginacion-reportes-ul",
        itemsPorPagina: 10
    });

    function aplicarFiltros() {
        const texto = busquedaGlobal.value.toLowerCase().trim();
        const estado = filtroEstado.value.toUpperCase();
        const prioridad = filtroPrioridad.value.toUpperCase();
        const desde = filtroFechaDesde.value;
        const hasta = filtroFechaHasta.value;

        let visibles = 0;

        filas.forEach(fila => {
            const contenidoFila = fila.innerText.toLowerCase();
            const filaEstado = fila.getAttribute("data-estado").toUpperCase();
            const filaPrioridad = fila.getAttribute("data-prioridad").toUpperCase();
            const filaFecha = fila.getAttribute("data-fecha");

            const cumpleTexto = texto === "" || contenidoFila.includes(texto);
            const cumpleEstado = estado === "" || filaEstado === estado;
            const cumplePrioridad = prioridad === "" || filaPrioridad === prioridad;

            let cumpleFecha = true;
            if (desde !== "" && filaFecha < desde) cumpleFecha = false;
            if (hasta !== "" && filaFecha > hasta) cumpleFecha = false;

            if (cumpleTexto && cumpleEstado && cumplePrioridad && cumpleFecha) {
                fila.dataset.coincide = "1";
                visibles++;
            } else {
                fila.dataset.coincide = "0";
                fila.style.display = "none";
            }
        });

        if (contadorFilas) contadorFilas.textContent = visibles;
        if (contadorExcel) contadorExcel.textContent = visibles;

        // Volvemos a la página 1 cada vez que cambian los filtros
        paginadorReportes.reset();
    }

    if (busquedaGlobal) busquedaGlobal.addEventListener("input", aplicarFiltros);
    if (filtroEstado) filtroEstado.addEventListener("change", aplicarFiltros);
    if (filtroPrioridad) filtroPrioridad.addEventListener("change", aplicarFiltros);
    if (filtroFechaDesde) filtroFechaDesde.addEventListener("change", aplicarFiltros);
    if (filtroFechaHasta) filtroFechaHasta.addEventListener("change", aplicarFiltros);

    window.limpiarFiltros = function() {
        busquedaGlobal.value = "";
        filtroEstado.value = "";
        filtroPrioridad.value = "";
        filtroFechaDesde.value = "";
        filtroFechaHasta.value = "";
        aplicarFiltros();
    };

// Exporta TODAS las filas que coinciden con el filtro, sin importar en qué página estén
    window.descargarReporteFiltrado = function() {
        // Usamos dataset.coincide porque es nuestra "fuente de verdad" (State)
        // No usamos style.display porque el paginador lo altera constantemente.
        const idsCoincidentes = filas
            .filter(f => f.dataset.coincide === "1")
            .map(f => f.getAttribute("data-id"));

        if (idsCoincidentes.length === 0) {
            alert("No hay incidencias que coincidan con los filtros para exportar.");
            return;
        }

        window.location.href = "/admin/reporte/excel-selectivo?ids=" + idsCoincidentes.join(",");
    };

    // Estado inicial: todas coinciden, primera página
    aplicarFiltros();
});