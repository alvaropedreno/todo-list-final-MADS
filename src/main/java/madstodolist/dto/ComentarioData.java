package madstodolist.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ComentarioData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long usuarioId;
    private String comentario;
    private LocalDateTime fecha;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComentarioData)) return false;
        ComentarioData that = (ComentarioData) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}