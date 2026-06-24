/* ==========================================
   LÓGICA DE INTERFAZ Y COMPONENTES
   ========================================== */
document.addEventListener("DOMContentLoaded", function() {
    
    // ------------------------------------------
    // 1. MODO OSCURO / CLARO (Con memoria persistente)
    // ------------------------------------------
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeIcon');
    const htmlElement = document.documentElement;

    if (themeToggleBtn && themeIcon) {
        const temaGuardado = localStorage.getItem('tema_preferido');
        
        if (temaGuardado === 'dark') {
            htmlElement.setAttribute('data-theme', 'dark');
            themeIcon.classList.remove('fa-moon');
            themeIcon.classList.add('fa-sun'); 
        } else {
            htmlElement.removeAttribute('data-theme');
            themeIcon.classList.remove('fa-sun');
            themeIcon.classList.add('fa-moon'); 
        }

        themeToggleBtn.addEventListener('click', () => {
            const currentTheme = htmlElement.getAttribute('data-theme');
            
            if (currentTheme === 'dark') {
                htmlElement.removeAttribute('data-theme');
                themeIcon.classList.remove('fa-sun');
                themeIcon.classList.add('fa-moon');
                localStorage.setItem('tema_preferido', 'light');
            } else {
                htmlElement.setAttribute('data-theme', 'dark');
                themeIcon.classList.remove('fa-moon');
                themeIcon.classList.add('fa-sun');
                localStorage.setItem('tema_preferido', 'dark');
            }
        });
    }

    // ------------------------------------------
    // 2. BUSCADOR DE TEXTO Y FILTRO DE BOTONES
    // ------------------------------------------
    
    // Detectamos si existe un buscador (soporta Docente, Admin o TI)
    const buscador = document.getElementById('buscadorDocente') || 
                     document.getElementById('buscadorAdmin') || 
                     document.getElementById('buscadorTI');
                     
    // Seleccionamos todas las tarjetas de incidencia
    const tarjetas = document.querySelectorAll('.incidencia-item-card');
    
    // Guardamos el estado actual seleccionado por los botones
    let estadoActual = 'TODAS';

    // Función global que es llamada desde el HTML (onclick="filtrarIncidencias(...)")
    window.filtrarIncidencias = function(estadoSeleccionado) {
        estadoActual = estadoSeleccionado;
        
        // Efecto visual: Apagar todos los botones y encender el que se clickeó
        const botonClickeado = event.currentTarget;
        const contenedorBotones = botonClickeado.parentElement;
        
        contenedorBotones.querySelectorAll('.btn').forEach(b => {
            b.classList.remove('btn-dark', 'active');
            b.classList.add('btn-light', 'border');
        });
        botonClickeado.classList.remove('btn-light', 'border');
        botonClickeado.classList.add('btn-dark', 'active');

        // Ejecutar el filtro
        aplicarFiltroCombinado();
    };

    // Escuchar cada vez que el usuario escribe en la barra de búsqueda
    if (buscador) {
        buscador.addEventListener('input', aplicarFiltroCombinado);
    }

    // Lógica maestra: Combina lo escrito en el buscador con el botón presionado
    function aplicarFiltroCombinado() {
        const textoBuscado = buscador ? buscador.value.toLowerCase() : '';

        tarjetas.forEach(tarjeta => {
            const estadoTarjeta = tarjeta.getAttribute('data-estado').toUpperCase();
            
            // Busca en todo el texto de la tarjeta (título, ubicación, categoría)
            const contenidoTarjeta = tarjeta.innerText.toLowerCase();

            // Verificamos si cumple ambas condiciones
            const coincideEstado = (estadoActual === 'TODAS' || estadoTarjeta === estadoActual);
            const coincideTexto = contenidoTarjeta.includes(textoBuscado);

            if (coincideEstado && coincideTexto) {
                tarjeta.style.display = 'block'; // Mostrar
            } else {
                tarjeta.style.display = 'none';  // Ocultar
            }
        });
    }
});