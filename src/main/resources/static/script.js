/* script.js */

const themeToggleBtn = document.getElementById('themeToggleBtn');
const themeIcon = document.getElementById('themeIcon');
const htmlElement = document.documentElement; // Etiqueta <html>

themeToggleBtn.addEventListener('click', () => {
    // Verifica el tema actual
    const currentTheme = htmlElement.getAttribute('data-theme');
    
    if (currentTheme === 'dark') {
        // Cambiar a Modo Claro
        htmlElement.removeAttribute('data-theme');
        themeIcon.classList.remove('fa-sun');
        themeIcon.classList.add('fa-moon');
    } else {
        // Cambiar a Modo Oscuro
        htmlElement.setAttribute('data-theme', 'dark');
        themeIcon.classList.remove('fa-moon');
        themeIcon.classList.add('fa-sun');
    }
});

// ==========================================
// LÓGICA DE NOTIFICACIONES TI (AJAX POLLING)
// ==========================================

// Buscamos el elemento HTML que tiene el total de pendientes
const contadorElement = document.getElementById('contadorPendientes');

// Solo ejecutamos el polling si el contador existe (es decir, si estamos en el panel TI)
if (contadorElement) {
    // Leemos el número directamente del HTML
    let conteoActual = parseInt(contadorElement.innerText) || 0;

    setInterval(function() {
        // Hacemos la consulta silenciosa al servidor
        fetch('/incidencias/api/conteo-pendientes')
            .then(respuesta => respuesta.json())
            .then(nuevoConteo => {
                
                // Si el número nuevo es mayor al que teníamos, ¡entró un nuevo ticket!
                if (nuevoConteo > conteoActual) {
                    
                    // Reproducimos un pequeño sonido
                    let audio = new Audio('https://assets.mixkit.co/active_storage/sfx/2869/2869-preview.mp3');
                    audio.play().catch(e => console.log("Sonido bloqueado por el navegador"));

                    // Mostramos el Toast visual
                    const toastElement = document.getElementById('alertaNuevoTicket');
                    if (toastElement) {
                        const toast = new bootstrap.Toast(toastElement);
                        toast.show();
                    }
                    
                    // Actualizamos nuestro contador local y el texto en la pantalla visualmente
                    conteoActual = nuevoConteo;
                    contadorElement.innerText = nuevoConteo;
                    
                } else if (nuevoConteo < conteoActual) {
                    // Si alguien más resolvió un ticket, solo actualizamos el número en silencio
                    conteoActual = nuevoConteo;
                    contadorElement.innerText = nuevoConteo;
                }
            })
            .catch(error => console.error('Error verificando tickets:', error));
            
    }, 15000); 
}