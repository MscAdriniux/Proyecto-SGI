/* ==========================================
   LÓGICA DE INTERFAZ, FILTROS Y PAGINACIÓN
   ========================================== */
document.addEventListener("DOMContentLoaded", function() {
    
    // ------------------------------------------
    // 1. MODO OSCURO / CLARO
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
    // 2. VARIABLES DE BÚSQUEDA Y PAGINACIÓN
    // ------------------------------------------
    const buscador = document.getElementById('buscadorDocente') || 
                     document.getElementById('buscadorAdmin') || 
                     document.getElementById('buscadorTI');
                     
    const tarjetas = Array.from(document.querySelectorAll('.incidencia-item-card'));
    let estadoActual = 'TODAS';
    
    // Configuración de la paginación
    const ITEMS_POR_PAGINA = 5; // <--- Cambia este número si quieres ver más o menos tarjetas
    let paginaActual = 1;
    let tarjetasFiltradas = [];

    // ------------------------------------------
    // 3. EVENTOS DE LOS BOTONES Y BUSCADOR
    // ------------------------------------------
    window.filtrarIncidencias = function(estadoSeleccionado) {
        estadoActual = estadoSeleccionado;
        
        // Efecto visual en los botones
        const botonClickeado = event.currentTarget;
        const contenedorBotones = botonClickeado.parentElement;
        
        contenedorBotones.querySelectorAll('.btn').forEach(b => {
            b.classList.remove('btn-dark', 'active');
            b.classList.add('btn-light', 'border');
        });
        botonClickeado.classList.remove('btn-light', 'border');
        botonClickeado.classList.add('btn-dark', 'active');

        aplicarFiltroCombinado();
    };

    if (buscador) {
        buscador.addEventListener('input', aplicarFiltroCombinado);
    }

    // ------------------------------------------
    // 4. LÓGICA DE FILTRADO Y REPARTO DE PÁGINAS
    // ------------------------------------------
    function aplicarFiltroCombinado() {
        const textoBuscado = buscador ? buscador.value.toLowerCase() : '';
        tarjetasFiltradas = []; // Vaciamos la lista temporal

        tarjetas.forEach(tarjeta => {
            const estadoTarjeta = tarjeta.getAttribute('data-estado').toUpperCase();
            const contenidoTarjeta = tarjeta.innerText.toLowerCase();

            const coincideEstado = (estadoActual === 'TODAS' || estadoTarjeta === estadoActual);
            const coincideTexto = contenidoTarjeta.includes(textoBuscado);

            if (coincideEstado && coincideTexto) {
                tarjetasFiltradas.push(tarjeta); // Si pasa el filtro, se añade a la lista
            } else {
                tarjeta.style.display = 'none';  // Si no, se oculta inmediatamente
            }
        });

        // Reiniciamos a la página 1 cada vez que se busca o filtra algo
        paginaActual = 1; 
        mostrarPagina(paginaActual);
        renderizarPaginacion();
    }

    function mostrarPagina(pagina) {
        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        tarjetasFiltradas.forEach((tarjeta, index) => {
            if (index >= inicio && index < fin) {
                tarjeta.style.display = 'block'; // Mostrar solo las de esta página
            } else {
                tarjeta.style.display = 'none';  // Ocultar las demás
            }
        });
    }

    // ------------------------------------------
    // 5. DIBUJAR LOS BOTONES DE PAGINACIÓN
    // ------------------------------------------
    function renderizarPaginacion() {
        const paginacionUl = document.getElementById('paginacion-ul');
        const paginacionContainer = document.getElementById('paginacion-container');

        if (!paginacionUl || !paginacionContainer) return;

        const totalPaginas = Math.ceil(tarjetasFiltradas.length / ITEMS_POR_PAGINA);
        paginacionUl.innerHTML = ''; // Limpiar botones anteriores

        if (totalPaginas <= 1) {
            paginacionContainer.style.display = 'none'; // Ocultar si solo hay 1 página
            return;
        }

        paginacionContainer.style.display = 'block'; // Mostrar si hay más de 1 página

        // Crear Botón "Anterior"
        const btnPrev = document.createElement('li');
        btnPrev.className = `page-item ${paginaActual === 1 ? 'disabled' : ''}`;
        btnPrev.innerHTML = `<a class="page-link" href="#" style="color: var(--text-main); background-color: var(--bg-card); border-color: var(--border-input);">Anterior</a>`;
        btnPrev.addEventListener('click', (e) => {
            e.preventDefault();
            if (paginaActual > 1) {
                paginaActual--;
                mostrarPagina(paginaActual);
                renderizarPaginacion();
            }
        });
        paginacionUl.appendChild(btnPrev);

        // Crear Números de página
        for (let i = 1; i <= totalPaginas; i++) {
            const li = document.createElement('li');
            li.className = `page-item ${paginaActual === i ? 'active' : ''}`;
            const estiloActivo = paginaActual === i ? 'background-color: var(--bs-dark); border-color: var(--bs-dark); color: white;' : 'color: var(--text-main); background-color: var(--bg-card); border-color: var(--border-input);';
            li.innerHTML = `<a class="page-link" href="#" style="${estiloActivo}">${i}</a>`;
            li.addEventListener('click', (e) => {
                e.preventDefault();
                paginaActual = i;
                mostrarPagina(paginaActual);
                renderizarPaginacion();
            });
            paginacionUl.appendChild(li);
        }

        // Crear Botón "Siguiente"
        const btnNext = document.createElement('li');
        btnNext.className = `page-item ${paginaActual === totalPaginas ? 'disabled' : ''}`;
        btnNext.innerHTML = `<a class="page-link" href="#" style="color: var(--text-main); background-color: var(--bg-card); border-color: var(--border-input);">Siguiente</a>`;
        btnNext.addEventListener('click', (e) => {
            e.preventDefault();
            if (paginaActual < totalPaginas) {
                paginaActual++;
                mostrarPagina(paginaActual);
                renderizarPaginacion();
            }
        });
        paginacionUl.appendChild(btnNext);
    }

    // Inicializar todo al cargar la página
    if (tarjetas.length > 0) {
        aplicarFiltroCombinado();
    }
});