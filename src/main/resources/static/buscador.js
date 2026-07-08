// =========================================================
// VARIABLES GLOBALES Y VALIDACIÓN MAESTRA
// =========================================================
let catalogoIncidencias = [];
const btnCrear = document.getElementById('btnCrear');
const tipoReal = document.getElementById('tipoIncidenciaReal');
const ubicacionRealTexto = document.getElementById('ubicacionRealTexto');
const idAulaReal = document.getElementById('idAulaReal');

// Esta función es la ÚNICA autorizada para encender el botón
function validarBotonGuardar() {
    if (tipoReal.value.trim() !== "" && ubicacionRealTexto.value.trim() !== "") {
        btnCrear.disabled = false;
    } else {
        btnCrear.disabled = true;
    }
}

// =========================================================
// CATÁLOGO DE INCIDENCIAS
// =========================================================
document.addEventListener("DOMContentLoaded", function() {
    // Cargar datos desde el backend
    fetch('/api/catalogo')
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            catalogoIncidencias = data;
        })
        .catch(error => console.error("Error al cargar el catálogo de incidencias:", error));

    const inputBuscador = document.getElementById('buscador');
    const listaSugerencias = document.getElementById('listaSugerencias');
    const catVisual = document.getElementById('categoriaVisual');
    const priVisual = document.getElementById('prioridadVisual');
    const catReal = document.getElementById('categoriaReal');
    const priReal = document.getElementById('prioridadReal');

    if (inputBuscador && listaSugerencias) {
        inputBuscador.addEventListener('input', function() {
            const textoBuscado = this.value.toLowerCase();
            listaSugerencias.innerHTML = '';
            
            // Invalida la selección previa al escribir
            tipoReal.value = "";
            validarBotonGuardar();

            if (textoBuscado.length === 0) {
                listaSugerencias.style.display = 'none';
                resetearCampos();
                return;
            }

            const coincidencias = catalogoIncidencias.filter(inc => 
                inc.nombre.toLowerCase().includes(textoBuscado)
            );

            if (coincidencias.length > 0) {
                listaSugerencias.style.display = 'block';
                coincidencias.forEach(inc => {
                    const div = document.createElement('div');
                    div.classList.add('sugerencia-item');
                    div.textContent = inc.nombre;
                    
                    div.addEventListener('click', () => {
                        inputBuscador.value = inc.nombre;
                        tipoReal.value = inc.nombre;
                        catVisual.value = inc.categoria;
                        catReal.value = inc.categoria;
                        priVisual.value = inc.prioridad;
                        priReal.value = inc.prioridad;

                        if(inc.prioridad === 'ALTA') priVisual.style.color = '#dc3545';
                        else if(inc.prioridad === 'MEDIA') priVisual.style.color = '#fd7e14';
                        else priVisual.style.color = '#198754';

                        listaSugerencias.style.display = 'none';
                        
                        // Llamar a la validación maestra en lugar de encender el botón directamente
                        validarBotonGuardar();
                    });
                    listaSugerencias.appendChild(div);
                });
            } else {
                listaSugerencias.style.display = 'none';
                resetearCampos();
            }
        });

        // Ocultar al hacer clic fuera y limpiar si no hay selección válida
        document.addEventListener('click', function(e) {
            if (e.target !== inputBuscador && !listaSugerencias.contains(e.target)) {
                listaSugerencias.style.display = 'none';
                if (tipoReal.value === "") {
                    inputBuscador.value = "";
                    resetearCampos();
                }
            }
        });
    }

    function resetearCampos() {
        catVisual.value = '';
        catReal.value = '';
        priVisual.value = '';
        priReal.value = '';
        tipoReal.value = '';
        priVisual.style.color = 'inherit';
        validarBotonGuardar();
    }
});

// =========================================================
// CATÁLOGO DE UBICACIONES
// =========================================================
document.addEventListener("DOMContentLoaded", function() {
    const buscadorAula = document.getElementById("buscadorAula");
    const listaAulas = document.getElementById("listaAulas");
    const itemsAulas = document.querySelectorAll(".aula-item");
    let ubicacionSeleccionadaValida = false;

    if(buscadorAula && listaAulas) {
        buscadorAula.addEventListener("input", function() {
            const filtro = this.value.toLowerCase();
            let hayResultados = false;
            
            // Invalida la selección previa al escribir
            ubicacionSeleccionadaValida = false;
            idAulaReal.value = "";
            ubicacionRealTexto.value = "";
            validarBotonGuardar();

            if (filtro.length > 0) {
                listaAulas.style.display = "block";
                itemsAulas.forEach(item => {
                    // Obtenemos el atributo de forma segura
                    const dataTexto = item.getAttribute("data-texto");
                    // Si no existe o es nulo, usamos una cadena vacía para evitar que rompa el script
                    const texto = dataTexto ? dataTexto.toLowerCase() : "";

                    if (texto.includes(filtro)) {
                        item.style.display = "block";
                        hayResultados = true;
                    } else {
                        item.style.display = "none";
                    }
                });
                
                if (!hayResultados) {
                    listaAulas.style.display = "none";
                }
            } else {
                listaAulas.style.display = "none";
            }
        });

        itemsAulas.forEach(item => {
            item.addEventListener("click", function() {
                const id = this.getAttribute("data-id");
                const texto = this.getAttribute("data-texto");

                buscadorAula.value = texto;
                idAulaReal.value = id;
                ubicacionRealTexto.value = texto;
                
                ubicacionSeleccionadaValida = true;
                listaAulas.style.display = "none";
                
                // Llamar a la validación maestra
                validarBotonGuardar();
            });
        });

        // Ocultar al hacer clic fuera y limpiar si no hay selección válida
        document.addEventListener("click", function(e) {
            if (e.target !== buscadorAula && !listaAulas.contains(e.target)) {
                listaAulas.style.display = "none";
                
                if (!ubicacionSeleccionadaValida) {
                    buscadorAula.value = "";
                    idAulaReal.value = "";
                    ubicacionRealTexto.value = "";
                    validarBotonGuardar();
                }
            }
        });
    }
});