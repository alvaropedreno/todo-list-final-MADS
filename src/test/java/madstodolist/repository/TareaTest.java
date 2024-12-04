package madstodolist.repository;


import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class TareaTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TareaRepository tareaRepository;

    //
    // Tests modelo Tarea en memoria, sin la conexión con la BD
    //

    @Test
    public void crearTarea() {
        // GIVEN
        // Un usuario nuevo creado en memoria, sin conexión con la BD,

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");

        // WHEN
        // se crea una nueva tarea con ese usuario,

        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

        // THEN
        // el título y el usuario de la tarea son los correctos.

        assertThat(tarea.getTitulo()).isEqualTo("Práctica 1 de MADS");
        assertThat(tarea.getUsuario()).isEqualTo(usuario);
    }

    @Test
    public void laListaDeTareasDeUnUsuarioSeActualizaEnMemoriaConUnaNuevaTarea() {
        // GIVEN
        // Un usuario nuevo creado en memoria, sin conexión con la BD,

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");

        // WHEN
        // se crea una tarea de ese usuario,

        Set<Tarea> tareas = usuario.getTareas();
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

        // THEN
        // la tarea creada se ha añadido a la lista de tareas del usuario.

        assertThat(usuario.getTareas()).contains(tarea);
        assertThat(tareas).contains(tarea);
    }

    @Test
    public void comprobarIgualdadTareasSinId() {
        // GIVEN
        // Creadas tres tareas sin identificador, y dos de ellas con
        // la misma descripción

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Tarea tarea1 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea2 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler");

        // THEN
        // son iguales (Equal) las tareas que tienen la misma descripción.

        assertThat(tarea1).isEqualTo(tarea2);
        assertThat(tarea1).isNotEqualTo(tarea3);
    }

    @Test
    public void comprobarIgualdadTareasConId() {
        // GIVEN
        // Creadas tres tareas con distintas descripciones y dos de ellas
        // con el mismo identificador,

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Tarea tarea1 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea2 = new Tarea(usuario, "Lavar la ropa");
        Tarea tarea3 = new Tarea(usuario, "Pagar el alquiler");
        tarea1.setId(1L);
        tarea2.setId(2L);
        tarea3.setId(1L);

        // THEN
        // son iguales (Equal) las tareas que tienen el mismo identificador.

        assertThat(tarea1).isEqualTo(tarea3);
        assertThat(tarea1).isNotEqualTo(tarea2);
    }

    //
    // Tests TareaRepository.
    // El código que trabaja con repositorios debe
    // estar en un entorno transactional, para que todas las peticiones
    // estén en la misma conexión a la base de datos, las entidades estén
    // conectadas y sea posible acceder a colecciones LAZY.
    //

    @Test
    @Transactional
    public void guardarTareaEnBaseDatos() {
        // GIVEN
        // Un usuario en la base de datos.

        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);

        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

        // WHEN
        // salvamos la tarea en la BD,

        tareaRepository.save(tarea);

        // THEN
        // se actualiza el id de la tarea,

        assertThat(tarea.getId()).isNotNull();

        // y con ese identificador se recupera de la base de datos la tarea
        // con los valores correctos de las propiedades y la relación con
        // el usuario actualizado también correctamente (la relación entre tarea
        // y usuario es EAGER).

        Tarea tareaBD = tareaRepository.findById(tarea.getId()).orElse(null);
        assertThat(tareaBD.getTitulo()).isEqualTo(tarea.getTitulo());
        assertThat(tareaBD.getUsuario()).isEqualTo(usuario);
    }

    @Test
    @Transactional
    public void salvarTareaEnBaseDatosConUsuarioNoBDLanzaExcepcion() {
        // GIVEN
        // Un usuario nuevo que no está en la BD
        // y una tarea asociada a ese usuario,

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

        // WHEN // THEN
        // se lanza una excepción al intentar salvar la tarea en la BD

        Assertions.assertThrows(Exception.class, () -> {
            tareaRepository.save(tarea);
        });
    }

    @Test
    @Transactional
    public void unUsuarioTieneUnaListaDeTareas() {
        // GIVEN
        // Un usuario con 2 tareas en la base de datos
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Long usuarioId = usuario.getId();

        Tarea tarea1 = new Tarea(usuario, "Práctica 1 de MADS");
        Tarea tarea2 = new Tarea(usuario, "Renovar el DNI");
        tareaRepository.save(tarea1);
        tareaRepository.save(tarea2);

        // WHEN
        // recuperamos el ususario de la base de datos,

        Usuario usuarioRecuperado = usuarioRepository.findById(usuarioId).orElse(null);

        // THEN
        // su lista de tareas también se recupera, porque se ha
        // definido la relación de usuario y tareas como EAGER.

        assertThat(usuarioRecuperado.getTareas()).hasSize(2);
    }

    @Test
    @Transactional
    public void añadirUnaTareaAUnUsuarioEnBD() {
        // GIVEN
        // Un usuario en la base de datos
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Long usuarioId = usuario.getId();

        // WHEN
        // Creamos una nueva tarea con el usuario recuperado de la BD
        // y la salvamos,

        Usuario usuarioBD = usuarioRepository.findById(usuarioId).orElse(null);
        Tarea tarea = new Tarea(usuarioBD, "Práctica 1 de MADS");
        tareaRepository.save(tarea);
        Long tareaId = tarea.getId();

        // THEN
        // la tarea queda guardada en la BD asociada al usuario

        Tarea tareaBD = tareaRepository.findById(tareaId).orElse(null);
        assertThat(tareaBD).isEqualTo(tarea);
        assertThat(tarea.getUsuario()).isEqualTo(usuarioBD);

        // y si recuperamos el usuario se obtiene la nueva tarea
        usuarioBD = usuarioRepository.findById(usuarioId).orElse(null);
        assertThat(usuarioBD.getTareas()).contains(tareaBD);
    }


    @Test
    @Transactional
    public void cambioEnLaEntidadEnTransactionalModificaLaBD() {
        // GIVEN
        // Un usuario y una tarea en la base de datos
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");
        tareaRepository.save(tarea);

        // Recuperamos la tarea
        Long tareaId = tarea.getId();
        tarea = tareaRepository.findById(tareaId).orElse(null);

        // WHEN
        // modificamos la descripción de la tarea

        tarea.setTitulo("Esto es una prueba");

        // THEN
        // la descripción queda actualizada en la BD.

        Tarea tareaBD = tareaRepository.findById(tareaId).orElse(null);
        assertThat(tareaBD.getTitulo()).isEqualTo(tarea.getTitulo());
    }

    @Test
    @Transactional
    public void testSubtareas() {
        // GIVEN
        // Un usuario en la base de datos.
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);

        // Una tarea principal
        Tarea tareaPrincipal = new Tarea(usuario, "Tarea Principal");
        tareaRepository.save(tareaPrincipal);

        // WHEN
        // Creamos y añadimos subtareas a la tarea principal
        Tarea subtarea1 = new Tarea(usuario, "Subtarea 1");
        Tarea subtarea2 = new Tarea(usuario, "Subtarea 2");
        tareaPrincipal.addSubtarea(subtarea1);
        tareaPrincipal.addSubtarea(subtarea2);
        tareaRepository.save(tareaPrincipal);

        // THEN
        // Verificamos que las subtareas se han añadido correctamente
        Tarea tareaPrincipalBD = tareaRepository.findById(tareaPrincipal.getId()).orElse(null);
        assertThat(tareaPrincipalBD).isNotNull();
        assertThat(tareaPrincipalBD.getSubtareas()).hasSize(2);
        assertThat(tareaPrincipalBD.getSubtareas()).contains(subtarea1, subtarea2);

        // Verificamos que las subtareas tienen la tarea principal como padre
        assertThat(subtarea1.getTareaPadre()).isEqualTo(tareaPrincipal);
        assertThat(subtarea2.getTareaPadre()).isEqualTo(tareaPrincipal);

        // Verificamos que se pueden eliminar subtareas
        tareaPrincipalBD.removeSubtarea(subtarea1);
        tareaRepository.save(tareaPrincipalBD);
        tareaPrincipalBD = tareaRepository.findById(tareaPrincipal.getId()).orElse(null);
        assertThat(tareaPrincipalBD.getSubtareas()).hasSize(1);
        assertThat(tareaPrincipalBD.getSubtareas()).doesNotContain(subtarea1);
    }

    @Test
    @Transactional
    public void testRetrieveSubtareas() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);

        Tarea tareaPrincipal = new Tarea(usuario, "Tarea Principal");
        Tarea subtarea1 = new Tarea(usuario, "Subtarea 1");
        Tarea subtarea2 = new Tarea(usuario, "Subtarea 2");
        tareaPrincipal.addSubtarea(subtarea1);
        tareaPrincipal.addSubtarea(subtarea2);
        tareaRepository.save(tareaPrincipal);

        // WHEN
        Tarea tareaPrincipalBD = tareaRepository.findById(tareaPrincipal.getId()).orElse(null);

        // THEN
        assertThat(tareaPrincipalBD).isNotNull();
        assertThat(tareaPrincipalBD.getSubtareas()).hasSize(2);
        assertThat(tareaPrincipalBD.getSubtareas()).contains(subtarea1, subtarea2);
    }

    @Test
    @Transactional
    public void testUpdateSubtarea() {
        // GIVEN
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);

        Tarea tareaPrincipal = new Tarea(usuario, "Tarea Principal");
        Tarea subtarea = new Tarea(usuario, "Subtarea");
        tareaPrincipal.addSubtarea(subtarea);
        tareaRepository.save(tareaPrincipal);

        // WHEN
        Tarea subtareaBD = tareaRepository.findById(subtarea.getId()).orElse(null);
        subtareaBD.setTitulo("Subtarea Actualizada");
        tareaRepository.save(subtareaBD);

        // THEN
        Tarea updatedSubtareaBD = tareaRepository.findById(subtarea.getId()).orElse(null);
        assertThat(updatedSubtareaBD.getTitulo()).isEqualTo("Subtarea Actualizada");
    }   
}
