package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Tarea;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class CalendarController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    @GetMapping("/calendario/{id}")
    public String calendario(@PathVariable(value="id") Long idUsuario,
                             @RequestParam(value = "mes", required = false) Integer mes,
                             @RequestParam(value = "anio", required = false) Integer anio,
                             Model model) {

        comprobarUsuarioLogeado(idUsuario);
        UsuarioData usuarioLoggeado = usuarioService.findById(idUsuario);

        LocalDate currentDate = LocalDate.now();
        int selectedMonth = (mes != null) ? mes : currentDate.getMonthValue();
        int selectedYear = (anio != null) ? anio : currentDate.getYear();

        Map<Integer, String> ocupado = new HashMap<>();

        List<TareaData> sustareas = tareaService.allTareasUsuarioByMonth(idUsuario, selectedMonth, selectedYear);

        // Principio del mes
        int primerDia = LocalDate.of(selectedYear, selectedMonth, 1).getDayOfWeek().getValue()-1;
        // Dias ocupados por tareas
        for (TareaData tarea : sustareas) {
            int dia = tarea.getDeadline().toLocalDate().getDayOfMonth();
            ocupado.put(dia, "tarea");
        }

        System.out.println("Ocupado: " + ocupado);


        model.addAttribute("ocupado", ocupado);
        model.addAttribute("sustareas", sustareas);
        model.addAttribute("usuarioLoggeado", usuarioLoggeado);
        model.addAttribute("usuario", usuarioLoggeado);
        model.addAttribute("mes", selectedMonth);
        model.addAttribute("anioSelected", selectedYear);
        model.addAttribute("primerDia", primerDia);
        model.addAttribute("ultimoDia", LocalDate.of(selectedYear, selectedMonth, 1).lengthOfMonth());

        return "calendario";
    }
}
