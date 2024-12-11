package madstodolist.service;

import madstodolist.model.Comentario;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.repository.ComentarioRepository;
import madstodolist.repository.TareaRepository;
import madstodolist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Comentario crearComentario(Long tareaId, Long usuarioId, String comentarioTexto) {
        Tarea tarea = tareaRepository.findById(tareaId).orElseThrow(() -> new TareaServiceException("No existe tarea id: " + tareaId));
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new TareaServiceException("No existe usuario id: " + usuarioId));

        if (comentarioTexto == null || comentarioTexto.isEmpty()) {
            throw new TareaServiceException("El comentario no puede estar vacío");
        }

        Comentario comentario = new Comentario();
        comentario.setTarea(tarea);
        comentario.setUsuario(usuario);
        comentario.setComentario(comentarioTexto);
        comentario.setFecha(LocalDateTime.now());
        comentarioRepository.save(comentario);

        tarea.addComentario(comentario);
        tareaRepository.save(tarea);

        return comentario;
    }

    @Transactional
    public void borrarComentario(Long comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        Tarea tarea = comentario.getTarea();
        tarea.getComentarios().remove(comentario);
        tareaRepository.save(tarea);
        comentarioRepository.deleteById(comentarioId);
    }

    @Transactional
    public void modificarComentario(Long comentarioId, String nuevoComentario) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        comentario.setComentario(nuevoComentario);
        comentarioRepository.save(comentario);
    }

    @Transactional
    public Comentario findById(Long comentarioId) {
        return comentarioRepository.findById(comentarioId).orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
    }
}