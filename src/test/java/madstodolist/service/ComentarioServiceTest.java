package madstodolist.service;

import madstodolist.model.Comentario;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.repository.ComentarioRepository;
import madstodolist.repository.TareaRepository;
import madstodolist.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class ComentarioServiceTest {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Test
    @Transactional
    public void testCrearComentario() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Tarea tarea = new Tarea(usuario, "Tarea de prueba");
        tareaRepository.save(tarea);

        // WHEN
        comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Este es un comentario de prueba");

        // THEN
        Tarea tareaConComentario = tareaRepository.findById(tarea.getId()).orElse(null);
        assertThat(tareaConComentario).isNotNull();
        assertThat(tareaConComentario.getComentarios()).hasSize(1);
        assertThat(tareaConComentario.getComentarios().get(0).getComentario()).isEqualTo("Este es un comentario de prueba");
    }

    @Test
    @Transactional
    public void testBorrarComentario() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Tarea tarea = new Tarea(usuario, "Tarea de prueba");
        tareaRepository.save(tarea);
        comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Comentario a borrar");
        Comentario comentario = tarea.getComentarios().get(0);

        // WHEN
        comentarioService.borrarComentario(comentario.getId());

        // THEN
        Tarea tareaSinComentario = tareaRepository.findById(tarea.getId()).orElse(null);
        assertThat(tareaSinComentario).isNotNull();
        assertThat(tareaSinComentario.getComentarios()).isEmpty();
    }

    @Test
    @Transactional
    public void testModificarComentario() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Tarea tarea = new Tarea(usuario, "Tarea de prueba");
        tareaRepository.save(tarea);
        comentarioService.crearComentario(tarea.getId(), usuario.getId(), "Comentario original");
        Comentario comentario = tarea.getComentarios().get(0);

        // WHEN
        comentarioService.modificarComentario(comentario.getId(), "Comentario modificado");

        // THEN
        Comentario comentarioModificado = comentarioRepository.findById(comentario.getId()).orElse(null);
        assertThat(comentarioModificado).isNotNull();
        assertThat(comentarioModificado.getComentario()).isEqualTo("Comentario modificado");
    }

    @Test
    @Transactional
    public void testCrearComentarioConTareaInexistente() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);

        // THEN
        assertThatThrownBy(() -> comentarioService.crearComentario(999L, usuario.getId(), "Comentario"))
                .isInstanceOf(TareaServiceException.class)
                .hasMessageContaining("No existe tarea id: 999");
    }

    @Test
    @Transactional
    public void testCrearComentarioConUsuarioInexistente() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario); // Save the usuario entity first
        Tarea tarea = new Tarea(usuario, "Tarea de prueba");
        tareaRepository.save(tarea);

        // THEN
        assertThatThrownBy(() -> comentarioService.crearComentario(tarea.getId(), 999L, "Comentario"))
                .isInstanceOf(TareaServiceException.class)
                .hasMessageContaining("No existe usuario id: 999");
    }

    @Test
    @Transactional
    public void testCrearComentarioVacio() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Tarea tarea = new Tarea(usuario, "Tarea de prueba");
        tareaRepository.save(tarea);

        // THEN
        assertThatThrownBy(() -> comentarioService.crearComentario(tarea.getId(), usuario.getId(), ""))
                .isInstanceOf(TareaServiceException.class)
                .hasMessageContaining("El comentario no puede estar vacío");
    }
}