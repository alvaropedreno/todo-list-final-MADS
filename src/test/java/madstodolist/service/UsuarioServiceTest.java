package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    // Método para inicializar los datos de prueba en la BD
    // Devuelve el identificador del usuario de la BD
    Long addUsuarioBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    @Test
    public void servicioLoginUsuario() {
        // GIVEN
        // Un usuario en la BD

        addUsuarioBD();

        // WHEN
        // intentamos logear un usuario y contraseña correctos
        UsuarioService.LoginStatus loginStatus1 = usuarioService.login("user@ua", "123");

        // intentamos logear un usuario correcto, con una contraseña incorrecta
        UsuarioService.LoginStatus loginStatus2 = usuarioService.login("user@ua", "000");

        // intentamos logear un usuario que no existe,
        UsuarioService.LoginStatus loginStatus3 = usuarioService.login("pepito.perez@gmail.com", "12345678");

        // THEN

        // el valor devuelto por el primer login es LOGIN_OK,
        assertThat(loginStatus1).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);

        // el valor devuelto por el segundo login es ERROR_PASSWORD,
        assertThat(loginStatus2).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);

        // y el valor devuelto por el tercer login es USER_NOT_FOUND.
        assertThat(loginStatus3).isEqualTo(UsuarioService.LoginStatus.USER_NOT_FOUND);
    }

    @Test
    public void servicioRegistroUsuario() {
        // WHEN
        // Registramos un usuario con un e-mail no existente en la base de datos,

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba2@gmail.com");
        usuario.setPassword("12345678");

        usuarioService.registrar(usuario);

        // THEN
        // el usuario se añade correctamente al sistema.

        UsuarioData usuarioBaseDatos = usuarioService.findByEmail("usuario.prueba2@gmail.com");
        assertThat(usuarioBaseDatos).isNotNull();
        assertThat(usuarioBaseDatos.getEmail()).isEqualTo("usuario.prueba2@gmail.com");
    }

    @Test
    public void servicioRegistroUsuarioExcepcionConNullPassword() {
        // WHEN, THEN
        // Si intentamos registrar un usuario con un password null,
        // se produce una excepción de tipo UsuarioServiceException

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }


    @Test
    public void servicioRegistroUsuarioExcepcionConEmailRepetido() {
        // GIVEN
        // Un usuario en la BD

        addUsuarioBD();

        // THEN
        // Si registramos un usuario con un e-mail ya existente en la base de datos,
        // , se produce una excepción de tipo UsuarioServiceException

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("12345678");

        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.registrar(usuario);
        });
    }

    @Test
    public void servicioRegistroUsuarioDevuelveUsuarioConId() {

        // WHEN
        // Si registramos en el sistema un usuario con un e-mail no existente en la base de datos,
        // y un password no nulo,

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("usuario.prueba@gmail.com");
        usuario.setPassword("12345678");

        UsuarioData usuarioNuevo = usuarioService.registrar(usuario);

        // THEN
        // se actualiza el identificador del usuario

        assertThat(usuarioNuevo.getId()).isNotNull();

        // con el identificador que se ha guardado en la BD.

        UsuarioData usuarioBD = usuarioService.findById(usuarioNuevo.getId());
        assertThat(usuarioBD).isEqualTo(usuarioNuevo);
    }

    @Test
    public void servicioConsultaUsuarioDevuelveUsuario() {
        // GIVEN
        // Un usuario en la BD

        Long usuarioId = addUsuarioBD();

        // WHEN
        // recuperamos un usuario usando su e-mail,

        UsuarioData usuario = usuarioService.findByEmail("user@ua");

        // THEN
        // el usuario obtenido es el correcto.

        assertThat(usuario.getId()).isEqualTo(usuarioId);
        assertThat(usuario.getEmail()).isEqualTo("user@ua");
        assertThat(usuario.getNombre()).isEqualTo("Usuario Ejemplo");
    }

    @Test
    public void servicioConsultaAdmin() {
        // GIVEN
        // Un usuario administrador en la BD

        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("admin@ua");
        usuario.setNombre("Admin Ejemplo");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuarioService.registrar(usuario);

        // WHEN
        // consultamos si existe algún usuario administrador en la BD

        boolean existeAdmin = usuarioService.existeAdmin();

        // THEN
        // el resultado es true

        assertThat(existeAdmin).isTrue();
    }

    @Test
    public void servicioCambiarPassword() {
        // GIVEN
        // Un usuario en la BD
        Long usuarioId = addUsuarioBD();

        // WHEN
        // Cambiamos la contraseña
        usuarioService.cambiarPassword(usuarioId, "123", "nuevaPassword123");

        // THEN
        // El usuario puede iniciar sesión con la nueva contraseña, pero no con la antigua
        UsuarioService.LoginStatus loginStatusConNueva = usuarioService.login("user@ua", "nuevaPassword123");
        UsuarioService.LoginStatus loginStatusConAntigua = usuarioService.login("user@ua", "123");

        assertThat(loginStatusConNueva).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);
        assertThat(loginStatusConAntigua).isEqualTo(UsuarioService.LoginStatus.ERROR_PASSWORD);
    }

    @Test
    public void servicioCambiarPasswordExcepcionSiPasswordActualEsIncorrecta() {
        // GIVEN
        // Un usuario en la BD
        Long usuarioId = addUsuarioBD();

        // WHEN, THEN
        // Intentar cambiar la contraseña con la actual incorrecta lanza una excepción
        Assertions.assertThrows(UsuarioServiceException.class, () -> {
            usuarioService.cambiarPassword(usuarioId, "passwordIncorrecta", "nuevaPassword123");
        });
    }

    @Test
    public void servicioEditarUsuario() throws IOException {
        // GIVEN
        // Un usuario en la BD
        Long usuarioId = addUsuarioBD();

        // Datos actualizados
        UsuarioData usuarioActualizado = new UsuarioData();
        usuarioActualizado.setId(usuarioId);
        usuarioActualizado.setNombre("Nombre Actualizado");
        usuarioActualizado.setEmail("nuevo.email@ua");

        // WHEN
        // Editamos los datos del usuario
        usuarioService.editarUsuario(usuarioActualizado);

        // THEN
        // Los datos se actualizan correctamente en la BD
        UsuarioData usuarioBD = usuarioService.findById(usuarioId);
        assertThat(usuarioBD.getNombre()).isEqualTo("Nombre Actualizado");
        assertThat(usuarioBD.getEmail()).isEqualTo("nuevo.email@ua");
    }

    @Test
    public void crearUsuarioConFoto() throws Exception {
        // GIVEN
        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");
        usuario.setNombre("Juan Gutiérrez");

        byte[] fotoContenido = "Contenido de prueba".getBytes();
        usuario.setFoto(fotoContenido);

        // THEN
        assertThat(usuario.getFoto()).isEqualTo(fotoContenido);
    }







}