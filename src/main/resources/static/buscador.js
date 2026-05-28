// Catálogo predefinido de incidencias
const catalogoIncidencias = [
    // Hardware y Equipamiento (Aulas y Laboratorios)
    { nombre: "Proyector no enciende", categoria: "Hardware", prioridad: "ALTA" },
    { nombre: "Computadora no da video", categoria: "Hardware", prioridad: "ALTA" },
    { nombre: "Estación de trabajo sobrecalentada / Falla de refrigeración", categoria: "Hardware", prioridad: "ALTA" },
    { nombre: "Falta cable HDMI / Adaptador", categoria: "Equipamiento", prioridad: "MEDIA" },
    { nombre: "Teclado o mouse inoperativo en laboratorio", categoria: "Equipamiento", prioridad: "BAJA" },
    
    // Redes y Conectividad
    { nombre: "No hay conexión a Internet en el aula (WiFi)", categoria: "Redes", prioridad: "ALTA" },
    { nombre: "Fallo en switch o puerto de red físico (Ethernet)", categoria: "Redes", prioridad: "ALTA" },
    { nombre: "Problema de enrutamiento o asignación de VLAN en laboratorio", categoria: "Redes", prioridad: "ALTA" },
    { nombre: "Latencia alta o red institucional lenta", categoria: "Redes", prioridad: "MEDIA" },

    // Software y Servidores
    { nombre: "Software AutoCAD / IDE de desarrollo no abre", categoria: "Software", prioridad: "MEDIA" },
    { nombre: "Licencia de programa expirada", categoria: "Software", prioridad: "MEDIA" },
    { nombre: "Error de permisos al ejecutar scripts en terminal de laboratorio", categoria: "Software", prioridad: "MEDIA" },
    { nombre: "Servidor local de base de datos sin respuesta", categoria: "Servidores", prioridad: "ALTA" },

    // Infraestructura Física
    { nombre: "Aire acondicionado gotea o no enfría", categoria: "Infraestructura", prioridad: "BAJA" },
    { nombre: "Pizarra en mal estado / Faltan plumones", categoria: "Infraestructura", prioridad: "BAJA" },
    { nombre: "Falla de energía eléctrica en el pabellón", categoria: "Infraestructura", prioridad: "ALTA" },

];

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

// Evento al escribir en el buscador
inputBuscador.addEventListener('input', function() {
    const textoBuscado = this.value.toLowerCase();
    listaSugerencias.innerHTML = '';
    
    // Si borra el texto, resetear campos
    if (textoBuscado.length === 0) {
        listaSugerencias.style.display = 'none';
        resetearCampos();
        return;
    }

    // Filtrar catálogo
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

// Ocultar lista si hace clic fuera
document.addEventListener('click', function(e) {
    if (e.target !== inputBuscador) {
        listaSugerencias.style.display = 'none';
    }
});

function resetearCampos() {
    catVisual.value = '';
    catReal.value = '';
    priVisual.value = '';
    priReal.value = '';
    tipoReal.value = '';
    priVisual.style.color = 'inherit';
    btnCrear.disabled = true;
}