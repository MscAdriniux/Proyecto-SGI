/* ==========================================
   PAGINACIÓN DE LA TABLA DE AUDITORÍA
   ========================================== */
document.addEventListener("DOMContentLoaded", function () {
    const paginadorAuditoria = crearPaginador({
        obtenerItems: () => Array.from(document.querySelectorAll('.fila-auditoria')),
        contenedorNavId: 'paginacion-auditoria-container',
        ulId: 'paginacion-auditoria-ul',
        itemsPorPagina: 10
    });

    paginadorAuditoria.render();
});