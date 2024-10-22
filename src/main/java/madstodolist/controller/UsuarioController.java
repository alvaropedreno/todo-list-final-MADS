package madstodolist.controller;

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

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model) {

        List<UsuarioData> usuarios = usuarioService.allUsuarios();
        model.addAttribute("usuarios", usuarios);

        return "listaUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String detalleUsuario(@PathVariable Long id, Model model) {
        UsuarioData usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        return "detalleUsuario";
    }


}
