/* ==========================================
   LÓGICA DE FILTROS DEL PANEL DE ADMINISTRADOR
   ========================================== */
let estadoFiltro = 'TODAS';
const inputBuscador = document.getElementById('buscadorAdmin');

function filtrarIncidencias(estado) {
    estadoFiltro = estado;
    aplicarFiltros();
    
    const buttons = document.querySelectorAll('#filterGroupAdmin .btn');
    buttons.forEach(btn => {
        const btnTexto = btn.innerText.trim().toUpperCase();
        const estadoComparar = estado.toUpperCase();

        if (btnTexto === estadoComparar || (estado === 'TODAS' && btnTexto === 'TODAS')) {
            btn.classList.remove('btn-light');
            btn.classList.add('btn-dark', 'active');
        } else {
            btn.classList.remove('btn-dark', 'active');
            btn.classList.add('btn-light');
        }
    });
}

if (inputBuscador) {
    inputBuscador.addEventListener('input', aplicarFiltros);
}

function aplicarFiltros() {
    const query = inputBuscador.value.toLowerCase();
    const cards = document.querySelectorAll('.incidencia-item-card');
    
    cards.forEach(card => {
        const estado = card.getAttribute('data-estado').toUpperCase();
        const texto = card.querySelector('.incidencia-titulo').textContent.toLowerCase();
        
        const coincideEstado = (estadoFiltro === 'TODAS') || (estado === estadoFiltro);
        const coincideTexto = texto.includes(query);
        
        if (coincideEstado && coincideTexto) {
            card.style.setProperty('display', 'block', 'important');
        } else {
            card.style.setProperty('display', 'none', 'important');
        }
    });
}

/* ==========================================
   LÓGICA DE NAVEGACIÓN (Panel Central)
   ========================================== */
function mostrarSeccion(seccion) {
    const secciones = ['incidencias', 'catalogo', 'reportes', 'metricas', 'auditoria'];
    
    secciones.forEach(sec => {
        const elem = document.getElementById(`seccion-${sec}`);
        if (elem) {
            elem.style.setProperty('display', (sec === seccion) ? 'block' : 'none', 'important');
        }
    });

    const botones = {
        incidencias: document.getElementById('btnVerIncidencias'),
        catalogo: document.getElementById('btnCrudCatalogo'),
        reportes: document.getElementById('btnCentroReportes'),
        metricas: document.getElementById('btnVerMetricas'),
        auditoria: document.getElementById('btnAuditoria')
    };

    Object.keys(botones).forEach(key => {
        const btn = botones[key];
        if (btn) {
            if (key === seccion) {
                btn.classList.remove('btn-light', 'border');
                btn.classList.add('btn-dark', 'active');
                btn.style.removeProperty('opacity');
            } else {
                btn.classList.remove('btn-dark', 'active');
                btn.classList.add('btn-light', 'border');
                btn.style.opacity = '0.8';
            }
        }
    });
}

// Cargar sección específica desde parámetros de la URL al iniciar
document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const seccion = urlParams.get('seccion');
    if (seccion) {
        mostrarSeccion(seccion);
    }
});