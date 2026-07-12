let chartTecnicosInstancia = null;
let chartCategoriasInstancia = null;

document.addEventListener("DOMContentLoaded", function() {
    // 1. Inicializar el Gráfico Circular Estático (Categorías)
    inicializarGraficoCategorias();

    // 2. Inicializar el Gráfico de Técnicos cargando los datos de "Hoy" por defecto
    cargarGraficoTecnicos('hoy');
});

// Función que consume la API REST de Spring Boot usando Fetch
function cargarGraficoTecnicos(periodo) {
    // Intercambio estético de botones activos del toggle
    const btnHoy = document.getElementById("btnToggleHoy");
    const btnSemana = document.getElementById("btnToggleSemana");
    
    if (periodo === 'semana') {
        btnHoy.classList.replace("btn-dark", "btn-light"); btnHoy.classList.add("border"); btnHoy.classList.remove("active");
        btnSemana.classList.replace("btn-light", "btn-dark"); btnSemana.classList.remove("border"); btnSemana.classList.add("active");
    } else {
        btnSemana.classList.replace("btn-dark", "btn-light"); btnSemana.classList.add("border"); btnSemana.classList.remove("active");
        btnHoy.classList.replace("btn-light", "btn-dark"); btnHoy.classList.remove("border"); btnHoy.classList.add("active");
    }

    // Petición AJAX asíncrona al endpoint del controlador
    fetch(`/api/metricas/tecnicos?periodo=${periodo}`)
        .then(response => response.json())
        .then(data => {
            const nombres = Object.keys(data);
            const cantidades = Object.values(data);

            const ctx = document.getElementById('canvasTecnicos').getContext('2d');
            
            // Si el gráfico ya existe, destruimos la instancia vieja para repintar limpiamente
            if (chartTecnicosInstancia) {
                chartTecnicosInstancia.destroy();
            }

            // Detectar colores según el tema de estilos.css
            const esModoOscuro = document.documentElement.getAttribute("data-theme") === "dark";
            const colorTexto = esModoOscuro ? "#e0e0e0" : "#111827";

            chartTecnicosInstancia = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: nombres.length > 0 ? nombres : ["Sin datos"],
                    datasets: [{
                        label: 'Tickets Resueltos',
                        data: cantidades.length > 0 ? cantidades : [0],
                        backgroundColor: '#0d6efd',
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false },
                        tooltip: { mode: 'index', intersect: false }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { color: colorTexto, stepSize: 1 },
                            grid: { color: esModoOscuro ? '#2d2d2d' : '#e5e7eb' }
                        },
                        x: {
                            ticks: { color: colorTexto },
                            grid: { display: false }
                        }
                    }
                }
            });
        })
        .catch(error => console.error("Error cargando la API de métricas de técnicos:", error));
}

function inicializarGraficoCategorias() {
    const ctx = document.getElementById('canvasCategorias').getContext('2d');
    const nombres = Object.keys(datosCategoriasIniciales);
    const cantidades = Object.values(datosCategoriasIniciales);

    const esModoOscuro = document.documentElement.getAttribute("data-theme") === "dark";
    const colorTexto = esModoOscuro ? "#e0e0e0" : "#111827";

    chartCategoriasInstancia = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: nombres.length > 0 ? nombres : ["Sin datos"],
            datasets: [{
                data: cantidades.length > 0 ? cantidades : [0],
                backgroundColor: ['#dc3545', '#ffc107', '#0dcaf0', '#198754', '#6f42c1'],
                borderWidth: esModoOscuro ? 2 : 1,
                borderColor: esModoOscuro ? '#141414' : '#ffffff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right',
                    labels: { color: colorTexto, font: { size: 11 } }
                }
            }
        }
    });
}