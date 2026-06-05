// 1. Inicializamos el catálogo vacío (se llenará dinámicamente desde la Base de Datos)
let catalogoIncidencias = [];

// 2. Al cargar la página, pedimos los datos al backend (Controlador REST de Diego)
document.addEventListener("DOMContentLoaded", function() {
    fetch('/api/catalogo')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            catalogoIncidencias = data; // Llenamos nuestro arreglo con los datos reales
            console.log("Catálogo cargado exitosamente desde la BD:", catalogoIncidencias);
        })
        .catch(error => console.error("Error al cargar el catálogo de incidencias:", error));
});

// 3. Capturamos todos los elementos del DOM
const inputBuscador = document.getElementById('buscador');
const listaSugerencias = document.getElementById('listaSugerencias');
const btnCrear = document.getElementById('btnCrear');

// Inputs visuales (los que ve el usuario)
const catVisual = document.getElementById('categoriaVisual');
const priVisual = document.getElementById('prioridadVisual');

// Inputs reales (los que se envían por POST a Java)
const catReal = document.getElementById('categoriaReal');
const priReal = document.getElementById('prioridadReal');
const tipoReal = document.getElementById('tipoIncidenciaReal');

// 4. Evento al escribir en el buscador
inputBuscador.addEventListener('input', function() {
    const textoBuscado = this.value.toLowerCase();
    listaSugerencias.innerHTML = '';
    
    // Si borra el texto, resetear campos
    if (textoBuscado.length === 0) {
        listaSugerencias.style.display = 'none';
        resetearCampos();
        return;
    }

    // Filtrar el catálogo dinámico traído de la base de datos
    const coincidencias = catalogoIncidencias.filter(inc => 
        inc.nombre.toLowerCase().includes(textoBuscado)
    );

    if (coincidencias.length > 0) {
        listaSugerencias.style.display = 'block';
        coincidencias.forEach(inc => {
            const div = document.createElement('div');
            div.classList.add('sugerencia-item');
            div.textContent = inc.nombre;
            
            // Al hacer clic en una sugerencia
            div.addEventListener('click', () => {
                inputBuscador.value = inc.nombre;
                tipoReal.value = inc.nombre;
                
                catVisual.value = inc.categoria;
                catReal.value = inc.categoria;
                
                priVisual.value = inc.prioridad;
                priReal.value = inc.prioridad;

                // Colorear el texto de prioridad para feedback visual
                if(inc.prioridad === 'ALTA') priVisual.style.color = '#dc3545';
                else if(inc.prioridad === 'MEDIA') priVisual.style.color = '#fd7e14';
                else priVisual.style.color = '#198754';

                listaSugerencias.style.display = 'none';
                btnCrear.disabled = false; // Habilitar botón de envío
            });
            listaSugerencias.appendChild(div);
        });
    } else {
        listaSugerencias.style.display = 'none';
        resetearCampos();
    }
});

// 5. Ocultar lista si hace clic fuera del buscador
document.addEventListener('click', function(e) {
    if (e.target !== inputBuscador) {
        listaSugerencias.style.display = 'none';
    }
});

// 6. Función para limpiar todos los campos
function resetearCampos() {
    catVisual.value = '';
    catReal.value = '';
    priVisual.value = '';
    priReal.value = '';
    tipoReal.value = '';
    priVisual.style.color = 'inherit';
    btnCrear.disabled = true;
}