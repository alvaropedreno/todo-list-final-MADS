package madstodolist.service;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD, USER_BLOCKED}

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);

        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!passwordEncoder.matches(password, usuario.get().getPassword())) {
            return LoginStatus.ERROR_PASSWORD;
        } else if (usuario.get().getBloqueado()) {
            return LoginStatus.USER_BLOCKED;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    // Se añade un usuario en la aplicación.
    // El email y password del usuario deben ser distinto de null
    // El email no debe estar registrado en la base de datos
    @Transactional
    public UsuarioData registrar(UsuarioData usuario) {
        String emailNormalizado = usuario.getEmail().trim().toLowerCase();

        // Validar email y contraseña
        if (usuario.getPassword() == null) {
            throw new UsuarioServiceException("El usuario no tiene password");
        }
        if (usuario.getEmail() == null) {
            throw new UsuarioServiceException("El usuario no tiene email");
        }

        // Verificar si el usuario ya existe
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(emailNormalizado);
        if (usuarioBD.isPresent()) {
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        }

        // Cifrar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardar el usuario
        Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);

        // Asignar la foto si está presente
        if (usuario.getFoto() != null) {
            usuarioNuevo.setFoto(usuario.getFoto()); // `getFoto()` devuelve `byte[]`
        }

        usuarioNuevo = usuarioRepository.save(usuarioNuevo);
        return modelMapper.map(usuarioNuevo, UsuarioData.class);
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) return null;
        else {
            return modelMapper.map(usuario, UsuarioData.class);
        }
    }

    // funcion que devuelve todos los usuarios registrados en la aplicación
    @Transactional(readOnly = true)
    public List<UsuarioData> allUsuarios() {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioData.class))
                .collect(java.util.stream.Collectors.toList());
    }

    // funcion que devuelve si existe algun usuario administrador en la aplicación
    @Transactional(readOnly = true)
    public boolean existeAdmin() {
        List<Usuario> usuarios = (List<Usuario>) usuarioRepository.findAll();
        for (Usuario usuario : usuarios) {
            if (usuario.getAdmin()) {
                return true;
            }
        }
        return false;
    }

    // funcion que cambia el estado de bloqueado de un usuario
    @Transactional
    public UsuarioData toggleBloqueo(Long usuarioID){
        Usuario usuario = usuarioRepository.findById(usuarioID).orElse(null);
        if (usuario == null) {
            throw new UsuarioServiceException("Usuario no encontrado");
        }
        usuario.setBloqueado(!usuario.getBloqueado());
        usuario = usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioData.class);
    }

    @Transactional
    public void editarUsuario(UsuarioData usuarioData) throws IOException {
        Usuario usuario = usuarioRepository.findById(usuarioData.getId()).orElseThrow(
                () -> new UsuarioServiceException("Usuario no encontrado"));

        // Actualizar datos básicos
        usuario.setNombre(usuarioData.getNombre());
        usuario.setEmail(usuarioData.getEmail());
        usuario.setFechaNacimiento(usuarioData.getFechaNacimiento());

        // Procesar la foto solo si existe
        MultipartFile foto = usuarioData.getFotoMultipartFile();
        if (foto != null && !foto.isEmpty()) {
            // Convertir MultipartFile a byte[]
            byte[] contenidoFoto = foto.getBytes();
            usuarioData.setFoto(contenidoFoto);
            usuario.setFoto(contenidoFoto);

            // Aquí puedes guardar el contenidoFoto en una base de datos o archivo
            System.out.println("Foto procesada correctamente, tamaño: " + contenidoFoto.length + " bytes");
        } else {
            System.out.println("No se subió ninguna foto o la foto está vacía.");
        }

        // Procesar otros datos del usuario (ejemplo)
        System.out.println("Actualizando usuario con nombre: " + usuarioData.getNombre());

        usuarioRepository.save(usuario);

    }




    @Transactional
    public void cambiarPassword(Long usuarioId, String currentPassword, String newPassword) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioServiceException("Usuario no encontrado"));

        // Verificar la contraseña actual
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            throw new UsuarioServiceException("La contraseña actual no es correcta.");
        }

        // Actualizar con la nueva contraseña cifrada
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }




}
