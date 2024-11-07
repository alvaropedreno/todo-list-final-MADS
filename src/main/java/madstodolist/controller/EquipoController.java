package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    EquipoService equipoService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/equipos")
    public String equipos(Model model) {

        Long usuarioID = managerUserSession.usuarioLogeado();

        List<EquipoData> equipos = equipoService.findAllOrdenadoPorNombre();
        //muestra equipos en consola
        for (EquipoData equipo : equipos) {
            System.out.println(equipo.getNombre());
        }
        model.addAttribute("equipos", equipos);
        model.addAttribute("usuarioLoggeado", usuarioService.findById(usuarioID));

        return "listaEquipos";
    }

    @GetMapping("/equipo/{id}/usuarios")
    public String usuariosEquipo(@PathVariable(value="id") Long idEquipo, Model model) {
        Long usuarioID = managerUserSession.usuarioLogeado();

        List<UsuarioData> usuarios = equipoService.usuariosEquipo(idEquipo);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioLoggeado", usuarioService.findById(usuarioID));
        model.addAttribute("equipo", equipoService.recuperarEquipo(idEquipo));

        return "listaUsuariosEquipo";
    }

}
