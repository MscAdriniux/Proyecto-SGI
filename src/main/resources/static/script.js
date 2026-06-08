/* ==========================================
   LÓGICA DE INTERFAZ Y COMPONENTES
   ========================================== */
document.addEventListener("DOMContentLoaded", function() {
    
    // ------------------------------------------
    // 1. MODO OSCURO / CLARO (Con memoria persistente)
    // ------------------------------------------
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeIcon');
    const htmlElement = document.documentElement; // Etiqueta <html>

    if (themeToggleBtn && themeIcon) {
        // Leer la memoria (localStorage) al cargar la página
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

        // Cambiar tema y guardar en memoria al hacer clic
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
    // 2. FILTRADO DE TARJETAS (Panel Docente)
    // ------------------------------------------
    const botonesFiltro = document.querySelectorAll('#grupo-filtros .btn');
    const tarjetas = document.querySelectorAll('.tarjeta-incidencia');

    // Solo ejecutamos esto si estamos en la pantalla del Docente 
    if (botonesFiltro.length > 0 && tarjetas.length > 0) {
        botonesFiltro.forEach(boton => {
            boton.addEventListener('click', function() {
                
                // --- EFECTO VISUAL DEL BOTÓN ---
                // Apagamos todos los botones (los ponemos blancos)
                botonesFiltro.forEach(b => {
                    b.classList.remove('btn-dark', 'active');
                    b.classList.add('btn-light', 'border');
                });
                // Encendemos el botón que acabas de presionar (lo ponemos oscuro)
                this.classList.remove('btn-light', 'border');
                this.classList.add('btn-dark', 'active');

                // --- LÓGICA DEL FILTRO ---
                const filtro = this.getAttribute('data-filter');

                tarjetas.forEach(tarjeta => {
                    const estadoTarjeta = tarjeta.getAttribute('data-estado');
                    
                    // Si el filtro es "TODAS" o si coincide con el estado exacto de la tarjeta
                    if (filtro === 'TODAS' || estadoTarjeta.toUpperCase() === filtro) {
                        tarjeta.style.display = 'block'; 
                    } else {
                        tarjeta.style.display = 'none';  
                    }
                });
            });
        });
    }
});
