/* ==========================================
   LÓGICA EXCLUSIVA DEL PANEL DE ADMINISTRADOR
   ========================================== */

let estadoFiltro = 'TODAS';
const inputBuscador = document.getElementById('buscadorAdmin');

function filtrarIncidencias(estado) {
    estadoFiltro = estado;
    aplicarFiltros();
    
    // Actualizar clase activa en botones
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
   LÓGICA DE NAVEGACIÓN Y CRUD DE CATÁLOGO
   ========================================== */

function mostrarSeccion(seccion) {
    const secciones = ['incidencias', 'catalogo', 'reportes', 'metricas', 'auditoria'];
    
    secciones.forEach(sec => {
        const elem = document.getElementById(`seccion-${sec}`);
        if (elem) {
            if (sec === seccion) {
                elem.style.setProperty('display', 'block', 'important');
            } else {
                elem.style.setProperty('display', 'none', 'important');
            }
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

    if (seccion === 'catalogo') {
        cargarCatalogoItems();
    }
}

// Cargar sección específica desde parámetros de la URL si se especifica
document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const seccion = urlParams.get('seccion');
    if (seccion) {
        mostrarSeccion(seccion);
    }
});

// Cargar catálogo de incidencias desde la API
function cargarCatalogoItems() {
    fetch('/api/catalogo')
        .then(response => {
            if (response.status === 204) {
                return [];
            }
            if (!response.ok) throw new Error('Error al cargar catálogo');
            return response.json();
        })
        .then(data => {
            renderizarCatalogo(data);
        })
        .catch(error => {
            console.error("Error al cargar el catálogo:", error);
            mostrarMensajeTabla("Error al cargar el catálogo de incidencias.");
        });
}

function renderizarCatalogo(data) {
    const tbody = document.getElementById('tabla-catalogo-body');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    if (data.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-4 text-muted" style="background-color: var(--bg-card);">
                    <i class="fa-regular fa-folder-open mb-2 d-block" style="font-size: 2rem;"></i>
                    No hay incidencias registradas en el catálogo.
                </td>
            </tr>
        `;
        return;
    }
    
    data.forEach(item => {
        const tr = document.createElement('tr');
        tr.style.borderBottom = '1px solid var(--border-card)';
        
        let clasePrioridad = 'bg-success-subtle text-success border-success-subtle';
        if (item.prioridad === 'ALTA') {
            clasePrioridad = 'bg-danger-subtle text-danger border-danger-subtle';
        } else if (item.prioridad === 'MEDIA') {
            clasePrioridad = 'bg-warning-subtle text-warning border-warning-subtle';
        }
        
        const nombreEscapado = item.nombre.replace(/`/g, '\\`').replace(/"/g, '&quot;');
        
        tr.innerHTML = `
            <td style="color: var(--text-main); background-color: var(--bg-card);">${item.id}</td>
            <td class="fw-bold" style="color: var(--text-main); background-color: var(--bg-card);">${item.nombre}</td>
            <td style="background-color: var(--bg-card);"><span class="badge bg-light border text-dark px-2 py-1" style="font-size: 0.8rem;">${item.categoria}</span></td>
            <td style="background-color: var(--bg-card);">
                <span class="badge rounded-pill px-3 py-1 border ${clasePrioridad}" style="font-size: 0.8rem;">
                    ${item.prioridad}
                </span>
            </td>
            <td class="text-end" style="background-color: var(--bg-card);">
                <button class="btn btn-sm btn-outline-primary me-2 px-3 py-1 rounded-3" onclick="abrirModalCatalogoEditar(${item.id}, \`${nombreEscapado}\`, '${item.categoria}', '${item.prioridad}')">
                    <i class="fa-solid fa-pen-to-square me-1"></i> Editar
                </button>
                <button class="btn btn-sm btn-outline-danger px-3 py-1 rounded-3" onclick="eliminarCatalogoItem(${item.id})">
                    <i class="fa-solid fa-trash-can me-1"></i> Eliminar
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function mostrarMensajeTabla(mensaje) {
    const tbody = document.getElementById('tabla-catalogo-body');
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-4 text-danger fw-bold" style="background-color: var(--bg-card);">
                    ${mensaje}
                </td>
            </tr>
        `;
    }
}

// Variables del Modal
let modalCatalogoInstancia = null;

function getModalCatalogo() {
    if (!modalCatalogoInstancia) {
        modalCatalogoInstancia = new bootstrap.Modal(document.getElementById('modalCatalogo'));
    }
    return modalCatalogoInstancia;
}

function abrirModalCatalogoNuevo() {
    document.getElementById('formCatalogo').reset();
    document.getElementById('catalogoId').value = '';
    document.getElementById('modalCatalogoLabel').innerText = 'Agregar Incidencia al Catálogo';
    getModalCatalogo().show();
}

function abrirModalCatalogoEditar(id, nombre, categoria, prioridad) {
    document.getElementById('catalogoId').value = id;
    document.getElementById('catalogoNombre').value = nombre;
    document.getElementById('catalogoCategoria').value = categoria;
    document.getElementById('catalogoPrioridad').value = prioridad;
    document.getElementById('modalCatalogoLabel').innerText = 'Editar Incidencia en el Catálogo';
    getModalCatalogo().show();
}

function guardarCatalogoItem(event) {
    event.preventDefault();
    
    const id = document.getElementById('catalogoId').value;
    const nombre = document.getElementById('catalogoNombre').value;
    const categoria = document.getElementById('catalogoCategoria').value;
    const prioridad = document.getElementById('catalogoPrioridad').value;
    
    const payload = {
        nombre: nombre,
        categoria: categoria,
        prioridad: prioridad
    };
    
    const url = id ? `/api/catalogo/${id}` : '/api/catalogo';
    const method = id ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) throw new Error('Error al guardar el item del catálogo');
        return response.json();
    })
    .then(() => {
        getModalCatalogo().hide();
        cargarCatalogoItems();
    })
    .catch(error => {
        console.error("Error al guardar:", error);
        alert("Ocurrió un error al intentar guardar la incidencia en el catálogo.");
    });
}

function eliminarCatalogoItem(id) {
    if (confirm("¿Estás seguro de que deseas eliminar esta incidencia del catálogo? Esto afectará a las sugerencias del buscador inteligente.")) {
        fetch(`/api/catalogo/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) throw new Error('Error al eliminar');
            cargarCatalogoItems();
        })
        .catch(error => {
            console.error("Error al eliminar:", error);
            alert("No se pudo eliminar la incidencia del catálogo.");
        });
    }
}