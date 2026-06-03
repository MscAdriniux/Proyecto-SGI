flowchart LR
    %% Actores
    U((Usuario Registrado))
    A((Administrador))

    %% Sistema
    subgraph Mi Aplicación
        direction TB
        UC1([Iniciar Sesión])
        UC2([Editar Perfil])
        UC3([Eliminar Cuentas])
    end

    %% Relaciones
    U --- UC1
    U --- UC2
    A --- UC1
    A --- UC3
