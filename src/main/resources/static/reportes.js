alert("REPORTES.JS CARGADO");
document.addEventListener("DOMContentLoaded", function() {
    const busquedaGlobal = document.getElementById("busquedaGlobal");
    const filtroEstado = document.getElementById("filtroEstado");
    const filtroPrioridad = document.getElementById("filtroPrioridad");
    const filtroFechaDesde = document.getElementById("filtroFechaDesde");
    const filtroFechaHasta = document.getElementById("filtroFechaHasta");
    
    const filas = document.querySelectorAll(".fila-incidencia");
    const contadorFilas = document.getElementById("contadorFilas");
    const contadorExcel = document.getElementById("contadorExcel");

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
                fila.style.display = "";
                visibles++;
            } else {
                fila.style.display = "none";
            }
        });

        if (contadorFilas) contadorFilas.textContent = visibles;
        if (contadorExcel) contadorExcel.textContent = visibles;
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

    window.descargarReporteFiltrado = function() {
        console.log("Entró a descargarReporteFiltrado");

        const idsVisibles = [];

        filas.forEach(fila => {
            if (fila.style.display !== "none") {
                idsVisibles.push(fila.getAttribute("data-id"));
            }
        });

        console.log("IDs visibles:", idsVisibles);
        console.log("Cantidad:", idsVisibles.length);

        if (idsVisibles.length === 0) {
            console.log("No hay IDs, redirigiendo...");
            window.location.href = "/admin/reporte/excel-selectivo";
            return;
        }

        console.log("Exportando...");
        window.location.href = "/admin/reporte/excel-selectivo?ids=" + idsVisibles.join(",");
    };
});