package madstodolist.model;

import com.sun.istack.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tareas")
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String titulo;

    private String descripcion;

    @Nullable
    private LocalDateTime deadline;



    @NotNull
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    private String prioridad; // Valores: "Alta", "Media", "Baja"

    private String estado; // Valores: "Pendiente", "En proceso", "Finalizada"

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    @ManyToOne
    @JoinColumn(name = "tarea_padre_id")
    private Tarea tareaPadre;

    @OneToMany(mappedBy = "tareaPadre", cascade = CascadeType.ALL)
    private List<Tarea> subtareas; // Initialize the list

    // Constructor vacío necesario para JPA/Hibernate.
    // No debe usarse desde la aplicación.
    public Tarea() {}

    // Al crear una tarea la asociamos automáticamente a un usuario
    public Tarea(Usuario usuario, String titulo) {
        this.titulo = titulo;
        this.estado = "Pendiente";
        this.comentarios = new ArrayList<>();
        this.subtareas = new ArrayList<>();
        setUsuario(usuario); // Esto añadirá la tarea a la lista de tareas del usuario
    }

    public Tarea(Usuario usuario, String titulo, String descripcion, String prioridad) {
        this.prioridad = prioridad;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = "Pendiente";
        this.comentarios = new ArrayList<>();
        this.subtareas = new ArrayList<>();
        setUsuario(usuario); // Esto añadirá la tarea a la lista de tareas del usuario
    }

    public Tarea(Usuario usuario, String titulo, String descripcion, LocalDateTime deadline) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.deadline = deadline;
        this.estado = "Pendiente";
        this.comentarios = new ArrayList<>();
        setUsuario(usuario); // Esto añadirá la tarea a la lista de tareas del usuario
        this.subtareas = new ArrayList<>();
    }

    public Tarea(Usuario usuario, String titulo, String descripcion, String prioridad, LocalDateTime deadline) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.deadline = deadline;
        this.prioridad = prioridad;
        this.estado = "Pendiente";
        this.comentarios = new ArrayList<>();
        setUsuario(usuario); // Esto añadirá la tarea a la lista de tareas del usuario
        this.subtareas = new ArrayList<>();
    }
    
    // Getters y setters básicos

    public Long getId() {
        return id;
    }

    public String getDescripcion() {return descripcion;}

    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Getters y setters de la relación muchos-a-uno con Usuario

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void addComentario(Comentario comentario) {
        comentarios.add(comentario);
    }

    public void removeComentario(Comentario comentario) {
        comentarios.remove(comentario);
        comentario.setTarea(null);
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    // Método para establecer la relación con el usuario

    public void setUsuario(Usuario usuario) {
        // Comprueba si el usuario ya está establecido
        if(this.usuario != usuario) {
            this.usuario = usuario;
            // Añade la tarea a la lista de tareas del usuario
            usuario.addTarea(this);
        }
    }

    public List<Tarea> getSubtareas() {
        return subtareas;
    }

    public void setSubtareas(List<Tarea> subtareas) {
        this.subtareas = subtareas;
    }

    public void addSubtarea(Tarea subtarea) {
        this.subtareas.add(subtarea);
        subtarea.setTareaPadre(this);
    }

    public void removeSubtarea(Tarea subtarea) {
        this.subtareas.remove(subtarea);
        subtarea.setTareaPadre(null);
    }

    public void setTareaPadre(Tarea tareaPadre) {
        this.tareaPadre = tareaPadre;
    }

    public Tarea getTareaPadre() {
        return tareaPadre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        if (id != null && tarea.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, tarea.id);
        // si no comparamos por campos obligatorios
        return titulo.equals(tarea.titulo) &&
                usuario.equals(tarea.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, usuario);
    }
}
