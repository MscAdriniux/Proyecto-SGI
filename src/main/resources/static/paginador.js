/* ==========================================
   PAGINADOR GENÉRICO REUTILIZABLE
   Usado en Auditoría, Centro de Reportes y CRUD Catálogo
   para no cargar tablas completas de una sola vez.
   ========================================== */

function crearPaginador({ obtenerItems, contenedorNavId, ulId, itemsPorPagina = 10 }) {
    let paginaActual = 1;

    function mostrarPagina() {
        const items = obtenerItems();
        const totalPaginas = Math.max(1, Math.ceil(items.length / itemsPorPagina));

        if (paginaActual > totalPaginas) paginaActual = totalPaginas;
        if (paginaActual < 1) paginaActual = 1;

        const inicio = (paginaActual - 1) * itemsPorPagina;
        const fin = inicio + itemsPorPagina;

        items.forEach((item, index) => {
            item.style.display = (index >= inicio && index < fin) ? '' : 'none';
        });

        dibujarControles(items.length, totalPaginas);
    }

    function dibujarControles(totalItems, totalPaginas) {
        const contenedor = document.getElementById(contenedorNavId);
        const ul = document.getElementById(ulId);
        if (!contenedor || !ul) return;

        ul.innerHTML = '';

        if (totalPaginas <= 1) {
            contenedor.style.display = 'none';
            return;
        }
        contenedor.style.display = 'block';

        const crearBoton = (texto, paginaDestino, deshabilitado, activo) => {
            const li = document.createElement('li');
            li.className = `page-item ${deshabilitado ? 'disabled' : ''} ${activo ? 'active' : ''}`;
            const estilo = activo
                ? 'background-color: var(--bs-dark); border-color: var(--bs-dark); color: white;'
                : 'color: var(--text-main); background-color: var(--bg-card); border-color: var(--border-input);';
            const a = document.createElement('a');
            a.className = 'page-link';
            a.href = '#';
            a.textContent = texto;
            a.style.cssText = estilo;
            li.appendChild(a);

            if (!deshabilitado) {
                li.addEventListener('click', (e) => {
                    e.preventDefault();
                    paginaActual = paginaDestino;
                    mostrarPagina();
                    contenedor.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                });
            }
            return li;
        };

        ul.appendChild(crearBoton('Anterior', paginaActual - 1, paginaActual === 1, false));
        for (let i = 1; i <= totalPaginas; i++) {
            ul.appendChild(crearBoton(i, i, false, i === paginaActual));
        }
        ul.appendChild(crearBoton('Siguiente', paginaActual + 1, paginaActual === totalPaginas, false));
    }

    return {
        render: mostrarPagina,
        reset() { paginaActual = 1; mostrarPagina(); },
        irAPagina(p) { paginaActual = p; mostrarPagina(); }
    };
}