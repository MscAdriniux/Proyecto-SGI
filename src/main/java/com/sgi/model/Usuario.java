ppackage com.sgi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad que representa un usuario del sistema (Docente, Soporte TI o Administrador).
 * Mapea directamente con la tabla "usuario" en la base de datos MySQL.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // Mapea tu columna id_usuario de MySQL a Java
    private Integer idUsuario;

    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasena;
    private String rol;

    /**
     * Constructor por defecto vacío, obligatorio para que JPA/Hibernate pueda instanciar la clase.
     */
    public Usuario() {
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    /**
     * Obtiene el identificador único del usuario en la base de datos.
     * @return El ID del usuario.
     */
    public Integer getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el identificador único del usuario.
     * @param idUsuario El nuevo ID a asignar.
     */
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene los nombres completos del usuario.
     * @return Los nombres registrados.
     */
    public String getNombres() {
        return nombres;
    }

    /**
     * Establece los nombres del usuario.
     * @param nombres Los nombres a registrar.
     */
    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    /**
     * Obtiene los apellidos del usuario.
     * @return Los apellidos registrados.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del usuario.
     * @param apellidos Los apellidos a registrar.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Obtiene el correo electrónico del usuario, que funciona como credencial de acceso.
     * @return El correo electrónico.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del usuario.
     * @param correo El correo a asignar.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene la contraseña del usuario.
     * @return La contraseña (normalmente encriptada/hash).
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     * En el flujo normal, esta contraseña debe venir encriptada desde el servicio.
     * @param contrasena La contraseña a registrar.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene el nivel de acceso del usuario en el sistema.
     * @return El rol (ej. docente, soporte ti, administrador).
     */
    public String getRol() {
        return rol;
    }

    /**
     * Establece el rol o nivel de acceso del usuario.
     * @param rol El rol a asignar.
     */
    public void setRol(String rol) {
        this.rol = rol;
    }
}