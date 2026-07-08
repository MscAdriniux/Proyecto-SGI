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