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
            // Avisamos a otros scripts (ej. metricas.js) de que el tema cambió,
            // para que puedan repintar gráficos u otros elementos con canvas.
            document.dispatchEvent(new CustomEvent('temaCambiado'));
        });
    }

    // ------------------------------------------
    // 2. VARIABLES DE BÚSQUEDA Y PAGINACIÓN
    // ------------------------------------------
    // Usamos 'let' para poder reasignarlos si ocurre un refresco por AJAX
    let buscador = document.getElementById('buscadorDocente') || 
                   document.getElementById('buscadorAdmin') || 
                   document.getElementById('buscadorTI');
                     
    let tarjetas = Array.from(document.querySelectorAll('.incidencia-item-card'));
    let estadoActual = 'TODAS';
    
    // Configuración de la paginación
    const ITEMS_POR_PAGINA = 5; 
    let paginaActual = 1;
    let tarjetasFiltradas = [];

    // ------------------------------------------
    // 3. EVENTOS DE LOS BOTONES Y BUSCADOR
    // ------------------------------------------
    window.filtrarIncidencias = function(estadoSeleccionado) {
        estadoActual = estadoSeleccionado;
        
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
        tarjetasFiltradas = []; 

        tarjetas.forEach(tarjeta => {
            const estadoTarjeta = tarjeta.getAttribute('data-estado').toUpperCase();
            const contenidoTarjeta = tarjeta.innerText.toLowerCase();

            const coincideEstado = (estadoActual === 'TODAS' || estadoTarjeta === estadoActual);
            const coincideTexto = contenidoTarjeta.includes(textoBuscado);

            if (coincideEstado && coincideTexto) {
                tarjetasFiltradas.push(tarjeta);
            } else {
                tarjeta.style.display = 'none';  
            }
        });

        paginaActual = 1; 
        mostrarPagina(paginaActual);
        renderizarPaginacion();
    }

    function mostrarPagina(pagina) {
        const inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        const fin = inicio + ITEMS_POR_PAGINA;

        tarjetasFiltradas.forEach((tarjeta, index) => {
            if (index >= inicio && index < fin) {
                tarjeta.style.display = 'block'; 
            } else {
                tarjeta.style.display = 'none';  
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
        paginacionUl.innerHTML = ''; 

        if (totalPaginas <= 1) {
            paginacionContainer.style.display = 'none'; 
            return;
        }

        paginacionContainer.style.display = 'block'; 

        // Botón "Anterior"
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

        // Números de página
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

        // Botón "Siguiente"
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

    if (tarjetas.length > 0) {
        aplicarFiltroCombinado();
    }
    
    // ------------------------------------------
    // 6. SISTEMA DE NOTIFICACIONES EN TIEMPO REAL CON AJAX
    // ------------------------------------------
    const badgeNotificaciones = document.getElementById('badgeNotificaciones');
    const contenedorNotificaciones = document.getElementById('contenedorNotificaciones');
    const mensajeSinNotificaciones = document.getElementById('mensajeSinNotificaciones');
    let contadorNuevas = 0;

    if (badgeNotificaciones) {
        
        // ------------------------------------------
        // 6. SISTEMA DE NOTIFICACIONES EN TIEMPO REAL ABSOLUTO (CROSS-PANEL)
        // ------------------------------------------
        // Inicializamos la conexión SSE de forma obligatoria para todos los paneles del sistema
        const eventSource = new EventSource('/api/notificaciones/suscripcion');

        eventSource.onmessage = function(event) {

            // 1. Sonido de alerta global
            const audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3');
            audio.play().catch(e => console.log("Sonido bloqueado por políticas del navegador"));

            // 2. Capturar elementos del panel de notificaciones del técnico (si existen)
            const badgeNotificaciones = document.getElementById('badgeNotificaciones');
            const contenedorNotificaciones = document.getElementById('contenedorNotificaciones');
            const mensajeSinNotificaciones = document.getElementById('mensajeSinNotificaciones');

            if (badgeNotificaciones) {
                let contadorActual = parseInt(badgeNotificaciones.textContent) || 0;
                contadorActual++;
                badgeNotificaciones.textContent = contadorActual;
                badgeNotificaciones.style.display = 'block';
            }

            if (mensajeSinNotificaciones) {
                mensajeSinNotificaciones.style.display = 'none';
            }

            // 3. PROCESAMIENTO INTELIGENTE DEL MENSAJE (PREFIJOS)
            let rawMensaje = event.data;
            let textoLimpio = rawMensaje;
            let etiquetaHtml = `<span class="badge bg-secondary-subtle text-secondary border border-secondary-subtle px-2 py-1">AVISO</span>`;
            let estiloBorde = "border-start border-4 border-secondary";

            if (rawMensaje.startsWith("NUEVO:")) {
                textoLimpio = rawMensaje.replace("NUEVO:", "").trim();
                etiquetaHtml = `<span class="badge bg-danger text-white px-2 py-1"><i class="fa-solid fa-triangle-exclamation me-1"></i>¡NUEVO TICKET!</span>`;
                estiloBorde = "border-start border-4 border-danger";
            } else if (rawMensaje.startsWith("PROCESO:")) {
                textoLimpio = rawMensaje.replace("PROCESO:", "").trim();
                etiquetaHtml = `<span class="badge bg-primary text-white px-2 py-1"><i class="fa-solid fa-screwdriver-wrench me-1"></i>EN PROCESO</span>`;
                estiloBorde = "border-start border-4 border-primary";
            } else if (rawMensaje.startsWith("ESCALADO:")) {
                textoLimpio = rawMensaje.replace("ESCALADO:", "").trim();
                etiquetaHtml = `<span class="badge bg-warning text-dark px-2 py-1"><i class="fa-solid fa-arrow-up-right-path me-1"></i>TICKET ESCALADO</span>`;
                estiloBorde = "border-start border-4 border-warning";
            } else if (rawMensaje.startsWith("RESUELTO:") || rawMensaje.startsWith("FINAL:")) {
                textoLimpio = rawMensaje.startsWith("RESUELTO:") ? rawMensaje.replace("RESUELTO:", "").trim() : rawMensaje.replace("FINAL:", "").trim();
                etiquetaHtml = `<span class="badge bg-success text-white px-2 py-1"><i class="fa-solid fa-circle-check me-1"></i>SOLUCIONADO</span>`;
                estiloBorde = "border-start border-4 border-success";
            }

            // Si la barra lateral de notificaciones existe en el DOM actual, inyectamos la tarjeta formateada
            if (contenedorNotificaciones) {
                const nuevaNotificacion = document.createElement('div');
                nuevaNotificacion.className = `p-3 mb-3 rounded-3 shadow-sm ${estiloBorde}`;
                nuevaNotificacion.style.backgroundColor = 'var(--bg-card)';

                nuevaNotificacion.innerHTML = `
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        ${etiquetaHtml}
                        <small class="text-muted fw-bold">Ahora mismo</small>
                    </div>
                    <p class="mb-0 mt-2" style="font-size: 0.9rem; line-height: 1.4;">${textoLimpio}</p>
                `;
                contenedorNotificaciones.insertBefore(nuevaNotificacion, contenedorNotificaciones.children[1] || contenedorNotificaciones.firstChild);
            }

            // ========================================================================
            // 4. ACTUALIZACIÓN DINÁMICA POR AJAX (Sincronización cross-panel de tarjetas)
            // ========================================================================
            const textoAntesDeRefrescar = buscador ? buscador.value : '';
            const estadoAntesDeRefrescar = estadoActual;

            fetch(window.location.href)
                .then(response => response.text())
                .then(html => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');

                    const nuevoContenedor = doc.querySelector('.incidencias-container');
                    const contenedorActual = document.querySelector('.incidencias-container');
                    if (nuevoContenedor && contenedorActual) {
                        contenedorActual.innerHTML = nuevoContenedor.innerHTML;
                    }

                    const nuevasMetricas = doc.querySelector('.row.mb-4.g-3');
                    const metricasActuales = document.querySelector('.row.mb-4.g-3');
                    if (nuevasMetricas && metricasActuales) {
                        metricasActuales.innerHTML = nuevasMetricas.innerHTML;
                    }

                    buscador = document.getElementById('buscadorDocente') || 
                               document.getElementById('buscadorAdmin') || 
                               document.getElementById('buscadorTI');

                    if (buscador) {
                        buscador.value = textoAntesDeRefrescar;
                        buscador.addEventListener('input', aplicarFiltroCombinado);
                    }

                    estadoActual = estadoAntesDeRefrescar;
                    const contenedorBotones = document.getElementById('filterGroupDocente') || 
                                             document.getElementById('filterGroupAdmin') || 
                                             document.getElementById('filterGroupTI');

                    if (contenedorBotones) {
                        contenedorBotones.querySelectorAll('.btn').forEach(b => {
                            const textoBoton = b.innerText.trim().toUpperCase();
                            let esActivo = false;

                            if (estadoActual === 'TODAS' && textoBoton === 'TODAS') esActivo = true;
                            if (estadoActual === 'PENDIENTE' && textoBoton === 'PENDIENTE') esActivo = true;
                            if (estadoActual === 'EN PROCESO' && textoBoton === 'EN PROCESO') esActivo = true;
                            if (estadoActual === 'RESUELTA' && (textoBoton === 'RESUELTA' || textoBoton === 'RESUELTAS')) esActivo = true;
                            if (estadoActual === 'ATENDIDA' && textoBoton === 'ATENDIDA') esActivo = true;

                            if (esActivo) {
                                b.classList.remove('btn-light', 'border');
                                b.classList.add('btn-dark', 'active');
                            } else {
                                b.classList.remove('btn-dark', 'active');
                                b.classList.add('btn-light', 'border');
                            }
                        });
                    }

                    tarjetas = Array.from(document.querySelectorAll('.incidencia-item-card'));

                    aplicarFiltroCombinado();
                    if (typeof verificarTiemposEspera === 'function') {
                        verificarTiemposEspera();
                    }
                })
                .catch(err => console.error('Error de sincronización cross-panel:', err));
        };

        eventSource.onerror = function(error) {
            console.error("Conexión de tiempo real interrumpida con el servidor. Reintentando...");
        };

        eventSource.onerror = function(error) {
            console.error("Conexión de notificaciones perdida. Reintentando...");
        };

        const offcanvasNotificaciones = document.getElementById('offcanvasNotificaciones');
        if (offcanvasNotificaciones) {
            offcanvasNotificaciones.addEventListener('show.bs.offcanvas', function () {
                contadorNuevas = 0;
                badgeNotificaciones.textContent = '0';
                badgeNotificaciones.style.display = 'none';
            });
        }
    }
    
    /* ==========================================
        FUNCIONES GLOBALES (MODALES Y ACCIONES)
        ========================================== */
     let modalResolucionInstancia = null;

     window.abrirModalResolucion = function(id) {
         // 1. Ponemos el ID oculto en el formulario
         document.getElementById('modalIncidenciaId').value = id;

         // 2. Inicializamos el modal de Bootstrap si no existe
         if (!modalResolucionInstancia) {
             modalResolucionInstancia = new bootstrap.Modal(document.getElementById('modalResolucion'));
         }

         // 3. Lo mostramos en pantalla
         modalResolucionInstancia.show();
     };
});

/* ==========================================
   7. CONTROL DE TIEMPOS DE ESPERA (SLA)
   ========================================== */
function verificarTiemposEspera() {
    // 1. Configuración de límites de tiempo en minutos según prioridad
    const LIMITES_MINUTOS = {
        'ALTA': 5,
        'MEDIA': 7,
        'BAJA': 10
    };
    
    // 2. Información de contacto del administrador
    const INFO_ADMIN = "Administrador de Soporte TI al +51 987 654 321";

    // 3. Obtener solo las tarjetas que están PENDIENTES
    const tarjetasPendientes = document.querySelectorAll('.incidencia-item-card[data-estado="PENDIENTE"]');
    const ahora = new Date();

    tarjetasPendientes.forEach(tarjeta => {
        const prioridad = tarjeta.getAttribute('data-prioridad');
        const fechaStr = tarjeta.getAttribute('data-fecha');
        
        if (!prioridad || !fechaStr) return;

        const fechaCreacion = new Date(fechaStr);
        // Calcular la diferencia en minutos
        const diferenciaMinutos = (ahora - fechaCreacion) / (1000 * 60);
        const limite = LIMITES_MINUTOS[prioridad] || 60;

        // Comprobar si ya existe la alerta para no duplicarla
        let alertaDiv = tarjeta.querySelector('.alerta-tiempo-espera');

        if (diferenciaMinutos >= limite) {
            if (!alertaDiv) {
                // Crear y agregar el mensaje de advertencia
                alertaDiv = document.createElement('div');
                alertaDiv.className = 'alerta-tiempo-espera alert alert-warning border-warning-subtle mt-3 mb-0 p-3 d-flex align-items-center shadow-sm rounded-3 fade show';
                alertaDiv.innerHTML = `
                    <div class="fs-4 me-3" style="color: inherit;">
                        <i class="fa-solid fa-clock-rotate-left"></i>
                    </div>
                    <div style="color: inherit;">
                        <strong class="d-block">Tiempo de espera prolongado</strong>
                        <span class="small">Esta incidencia lleva más de ${limite} minutos sin atención. Por favor, comuníquese con el <strong>${INFO_ADMIN}</strong>.</span>
                    </div>
                `;
                tarjeta.appendChild(alertaDiv);
            }
        }
    });
}

// Ejecutar la validación al cargar la página
document.addEventListener("DOMContentLoaded", function() {
    verificarTiemposEspera();
    // Volver a verificar cada 1 minuto (60000 milisegundos)
    setInterval(verificarTiemposEspera, 60000);
});