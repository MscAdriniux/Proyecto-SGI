/* script.js */

document.addEventListener("DOMContentLoaded", function() {
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeIcon');
    const htmlElement = document.documentElement; // Etiqueta <html>

    // 1. AL CARGAR LA PÁGINA: Leer la memoria (localStorage)
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

    // 2. AL HACER CLIC: Cambiar tema y guardar en memoria
    themeToggleBtn.addEventListener('click', () => {
        const currentTheme = htmlElement.getAttribute('data-theme');
        
        if (currentTheme === 'dark') {
            // Pasamos a Claro
            htmlElement.removeAttribute('data-theme');
            themeIcon.classList.remove('fa-sun');
            themeIcon.classList.add('fa-moon');
            localStorage.setItem('tema_preferido', 'light');
        } else {
            // Pasamos a Oscuro
            htmlElement.setAttribute('data-theme', 'dark');
            themeIcon.classList.remove('fa-moon');
            themeIcon.classList.add('fa-sun');
            localStorage.setItem('tema_preferido', 'dark');
        }
    });
});

document.addEventListener("DOMContentLoaded", function() {
    
    // 1. Atrapamos los botones y todas las tarjetas
    const botonesFiltro = document.querySelectorAll('#grupo-filtros .btn');
    const tarjetas = document.querySelectorAll('.tarjeta-incidencia');

    // 2. Le agregamos un "escuchador de clics" a cada botón
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
            // Leemos qué estado queremos buscar ("PENDIENTE", "RESUELTA", etc.)
            const filtro = this.getAttribute('data-filter');

            // Revisamos tarjeta por tarjeta
            tarjetas.forEach(tarjeta => {
                const estadoTarjeta = tarjeta.getAttribute('data-estado');
                
                // Si el filtro es "TODAS" o si coincide con el estado exacto de la tarjeta
                if (filtro === 'TODAS' || estadoTarjeta.toUpperCase() === filtro) {
                    tarjeta.style.display = 'block'; // Mostrar la tarjeta
                } else {
                    tarjeta.style.display = 'none';  // Ocultar la tarjeta
                }
            });
        });
    });
});