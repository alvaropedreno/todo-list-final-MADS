package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoAdminException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarUsuarioAdmin() {
        if (!managerUserSession.isAdmin())
            throw new UsuarioNoAdminException();
    }

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model) {

        comprobarUsuarioAdmin();
        Long usuarioID = managerUserSession.usuarioLogeado();

        List<UsuarioData> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioLoggeado", usuarioService.findById(usuarioID));

        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String detalleUsuario(@PathVariable Long id, Model model) {

        comprobarUsuarioAdmin();
        Long usuarioID = managerUserSession.usuarioLogeado();

        UsuarioData usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLoggeado", usuarioService.findById(usuarioID));
        return "detalleUsuario";
    }

    @GetMapping("/toggleBloqueo/{id}")
    public String toggleBloque(@PathVariable Long id) {
        usuarioService.toggleBloqueo(id);
        return "redirect:/registrados";
    }

    @GetMapping("/cuenta")
    public String cuentaUsuario(Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        return "cuentaUsuario";
    }

}
