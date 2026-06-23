/* ==========================================
   LÓGICA EXCLUSIVA DEL PANEL DE SOPORTE TÉCNICO
   ========================================== */

// 1. MODAL DE RESOLUCIÓN
let bootstrapModal = null;

function abrirModalResolucion(id) {
    document.getElementById('modalIncidenciaId').value = id;
    if(!bootstrapModal) {
        bootstrapModal = new bootstrap.Modal(document.getElementById('modalResolucion'));
    }
    bootstrapModal.show();
}

// 2. FILTRADO DINÁMICO
let estadoFiltro = 'TODAS';
const inputBuscador = document.getElementById('buscadorTI');

function filtrarIncidencias(estado) {
    estadoFiltro = estado;
    aplicarFiltros();
    
    // Actualizar clase activa en botones
    const buttons = document.querySelectorAll('#filterGroupTI .btn');
    buttons.forEach(btn => {
        if (btn.innerText.toUpperCase() === estado.toUpperCase() || 
            (estado === 'TODAS' && btn.innerText === 'Todas')) {
            btn.classList.remove('btn-light');
            btn.classList.add('btn-dark', 'active');
        } else {
            btn.classList.remove('btn-dark', 'active');
            btn.classList.add('btn-light');
        }
    });
}

if(inputBuscador) {
    inputBuscador.addEventListener('input', aplicarFiltros);
}

function aplicarFiltros() {
    const query = inputBuscador.value.toLowerCase();
    const cards = document.querySelectorAll('.incidencia-item-card');

    cards.forEach(card => {
        const estado = card.getAttribute('data-estado').toUpperCase();
        const texto = card.querySelector('.incidencia-titulo').textContent.toLowerCase();

        // Unificación: Si el filtro es RESUELTA, permite mostrar tanto RESUELTA como ATENDIDA
        let coincideEstado = (estadoFiltro === 'TODAS') || (estado === estadoFiltro);
        if (estadoFiltro === 'RESUELTA' && (estado === 'RESUELTA' || estado === 'ATENDIDA')) {
            coincideEstado = true;
        }

        const coincideTexto = texto.includes(query);

        if (coincideEstado && coincideTexto) {
            card.style.setProperty('display', 'block', 'important');
        } else {
            card.style.setProperty('display', 'none', 'important');
        }
    });
}

// ==========================================
// 3. NOTIFICACIONES SSE EN TIEMPO REAL
// ==========================================

function reproducirSonidoNotificacion() {
    try {
        const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
        const osc = audioCtx.createOscillator();
        const gain = audioCtx.createGain();
        
        osc.connect(gain);
        gain.connect(audioCtx.destination);
        
        osc.type = 'sine';
        osc.frequency.setValueAtTime(523.25, audioCtx.currentTime); // C5
        osc.frequency.exponentialRampToValueAtTime(783.99, audioCtx.currentTime + 0.15); // G5
        
        gain.gain.setValueAtTime(0.3, audioCtx.currentTime);
        gain.gain.exponentialRampToValueAtTime(0.01, audioCtx.currentTime + 0.30);
        
        osc.start();
        osc.stop(audioCtx.currentTime + 0.35);
    } catch(e) {
        console.log("AudioContext blocked or not supported by browser:", e);
    }
}

function mostrarToastNotificacion(tipo, ubicacion, prioridad) {
    const container = document.getElementById('toastContainer');
    if(!container) return; // Salvavidas si no existe el contenedor
    
    // Crear el toast
    const toast = document.createElement('div');
    toast.className = 'toast-notif';
    
    // Configurar color según prioridad
    let colorPrioridad = '#198754'; // BAJA
    if (prioridad === 'ALTA') colorPrioridad = '#dc3545';
    else if (prioridad === 'MEDIA') colorPrioridad = '#fd7e14';
    
    toast.innerHTML = `
        <div style="color: ${colorPrioridad}; font-size: 1.25rem;">
            <i class="fa-solid fa-circle-exclamation"></i>
        </div>
        <div style="flex-grow: 1;">
            <strong style="color: var(--text-main); font-size: 0.95rem; display: block; margin-bottom: 2px;">Nueva Incidencia Reportada</strong>
            <span style="color: var(--text-subtitle); font-size: 0.85rem; display: block; line-height: 1.3;">
                ${tipo} en <strong>${ubicacion}</strong>
            </span>
            <span class="badge rounded-pill mt-2" style="background-color: ${colorPrioridad}22; color: ${colorPrioridad}; border: 1px solid ${colorPrioridad}55; font-size: 0.75rem;">
                ${prioridad}
            </span>
        </div>
        <button type="button" class="btn-close" style="font-size: 0.75rem; margin-top: -4px; filter: var(--theme-icon-filter);" onclick="this.parentElement.remove()"></button>
    `;
    
    container.appendChild(toast);
    reproducirSonidoNotificacion();
    
    // Auto eliminar después de 8 segundos
    setTimeout(() => {
        toast.style.animation = 'fadeOut 0.3s cubic-bezier(0.16, 1, 0.3, 1) forwards';
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 8000);
}

// Inicializar Suscripción a SSE
document.addEventListener("DOMContentLoaded", function() {
    try {
        const eventSource = new EventSource('/api/notificaciones/suscripcion');
        
        eventSource.addEventListener('NUEVA_INCIDENCIA', function(event) {
            const data = JSON.parse(event.data);
            mostrarToastNotificacion(data.tipo, data.ubicacion, data.prioridad);
        });

        eventSource.addEventListener('error', function(e) {
            console.log("Error de conexión SSE, reintentando...", e);
        });
    } catch(err) {
        console.error("No se pudo iniciar el canal SSE:", err);
    }
});