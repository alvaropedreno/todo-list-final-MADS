package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class HomeController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping("/about")
    public String about(Model model) {

        Long usuarioID = managerUserSession.usuarioLogeado();

        if (usuarioID == null){
            model.addAttribute("usuarioLoggeado", null);
        }
        else {
            UsuarioData usuarioLoggeado = usuarioService.findById(usuarioID);
            model.addAttribute("usuarioLoggeado", usuarioLoggeado);

        }


        return "about";
    }

}
