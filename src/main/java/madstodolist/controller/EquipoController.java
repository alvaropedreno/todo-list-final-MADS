package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.controller.exception.UsuarioNoAdminException;
import madstodolist.dto.EquipoData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.EquipoService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class EquipoController {

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    EquipoService equipoService;
    @Autowired
    private UsuarioService usuarioService;

    private void comprobarUsuarioAdmin() {
        if (!managerUserSession.isAdmin())
            throw new UsuarioNoAdminException();
    }

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

    @GetMapping("/equipos/nuevo")
    public String formNuevoEquipo(@ModelAttribute EquipoData equipoData, Model model) {
        Long usuarioID = managerUserSession.usuarioLogeado();
        model.addAttribute("usuarioLoggeado", usuarioService.findById(usuarioID));
        return "formNuevoEquipo";
    }

    @PostMapping("/equipos/nuevo")
    public String nuevoEquipo(@ModelAttribute EquipoData equipoData, Model model, RedirectAttributes flash){
        equipoService.crearEquipo(equipoData.getNombre());
        flash.addFlashAttribute("mensaje", "Equipo creado correctamente");
        return "redirect:/equipos";
    }

    @GetMapping("/equipos/{id}/usuarios")
    public String usuariosEquipo(@PathVariable(value="id") Long idEquipo, Model model) {
        Long usuarioID = managerUserSession.usuarioLogeado();

        List<UsuarioData> usuarios = equipoService.usuariosEquipo(idEquipo);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioLoggeado", usuarioService.findById(usuarioID));
        model.addAttribute("equipo", equipoService.recuperarEquipo(idEquipo));
        model.addAttribute("pertenece", usuarios.contains(usuarioService.findById(usuarioID)));

        return "listaUsuariosEquipo";
    }

    @GetMapping("/equipos/{idEquipo}/addUsuario/{idUsuario}")
    public String addUsuarioEquipo(@PathVariable(value="idEquipo") Long idEquipo, @PathVariable(value="idUsuario") Long idUsuario, Model model) {

        Long idUsuarioLoggeado = managerUserSession.usuarioLogeado();
        equipoService.añadirUsuarioAEquipo(idEquipo, idUsuario);
        return "redirect:/equipos/" + idEquipo + "/usuarios";
    }

    @GetMapping("/equipos/{idEquipo}/deleteUsuario/{idUsuario}")
    public String deleteUsuarioEquipo(@PathVariable(value="idEquipo") Long idEquipo, @PathVariable(value="idUsuario") Long idUsuario, Model model) {

        Long idUsuarioLoggeado = managerUserSession.usuarioLogeado();
        equipoService.eliminarUsuarioDeEquipo(idEquipo, idUsuario);
        return "redirect:/equipos/" + idEquipo + "/usuarios";
    }

    @GetMapping("/equipos/{id}/editar")
    public String formEditarEquipo(@PathVariable(value="id") Long idEquipo, @ModelAttribute EquipoData equipoData, Model model) {

        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        comprobarUsuarioAdmin();
        Long usuarioID = managerUserSession.usuarioLogeado();

        model.addAttribute("usuarioLoggeado", usuarioService.findById(usuarioID));
        model.addAttribute("equipo", equipoService.recuperarEquipo(idEquipo));
        equipoData.setNombre(equipo.getNombre());
        return "formEditarEquipo";
    }

    @PostMapping("/equipos/{id}/editar")
    public String editarEquipo(@PathVariable(value="id") Long idEquipo, @ModelAttribute EquipoData equipoData, Model model, RedirectAttributes flash) {

        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }

        equipoService.editarEquipo(idEquipo, equipoData.getNombre());

        comprobarUsuarioAdmin();
        flash.addFlashAttribute("mensaje", "Equipo modificado correctamente");
        return "redirect:/equipos";
    }

    @DeleteMapping("/equipos/{id}")
    @ResponseBody
    public String borrarEquipo(@PathVariable(value="id") Long idEquipo, RedirectAttributes flash, HttpSession session) {
        System.out.println("*************");
        EquipoData equipo = equipoService.recuperarEquipo(idEquipo);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }
        //mostrar en consola el nombre del equipo
        System.out.println("****" + equipo.getNombre());
        comprobarUsuarioAdmin();
        System.out.println("Equipo a eliminar: " + idEquipo);
        equipoService.eliminarEquipo(idEquipo);
        System.out.println("Equipo eliminado correctamente");

        return "";
    }
}
