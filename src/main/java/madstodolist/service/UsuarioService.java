package madstodolist.service;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else if(usuario.get().getBloqueado()){
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
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else if (usuario.getEmail() == null)
            throw new UsuarioServiceException("El usuario no tiene email");
        else if (usuario.getPassword() == null)
            throw new UsuarioServiceException("El usuario no tiene password");
        else {
            Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);
            usuarioNuevo = usuarioRepository.save(usuarioNuevo);
            return modelMapper.map(usuarioNuevo, UsuarioData.class);
        }
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
    public UsuarioData editarUsuario(UsuarioData usuarioData) {
        // Buscar al usuario por su ID
        Usuario usuario = usuarioRepository.findById(usuarioData.getId()).orElseThrow(
                () -> new UsuarioServiceException("Usuario no encontrado"));

        // Verificar si el email pertenece a otro usuario
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuarioData.getEmail());
        if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuario.getId())) {
            throw new UsuarioServiceException("El usuario con email " + usuarioData.getEmail() + " ya está registrado");
        }

        // Actualizar los datos del usuario
        usuario.setNombre(usuarioData.getNombre());
        usuario.setEmail(usuarioData.getEmail());
        usuario.setFechaNacimiento(usuarioData.getFechaNacimiento());

        usuario = usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioData.class);
    }

}
