package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Comentario;
import madstodolist.service.ComentarioService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class TareaController {

    private static final Logger logger = LoggerFactory.getLogger(TareaController.class);

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    ComentarioService comentarioService;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    @GetMapping("/usuarios/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable(value="id") Long idUsuario,
                                 @ModelAttribute TareaData tareaData, Model model,
                                 HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        UsuarioData usuarioLoggeado = usuarioService.findById(idUsuario);
        model.addAttribute("usuarioLoggeado", usuarioLoggeado);
        model.addAttribute("usuario", usuarioLoggeado);
        return "formNuevaTarea";
    }

    @PostMapping("/usuarios/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable(value="id") Long idUsuario, @ModelAttribute TareaData tareaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        logger.info("Received TareaData: titulo={}, descripcion={}, deadline={}",
                tareaData.getTitulo(), tareaData.getDescripcion(), tareaData.getDeadline());

        LocalDateTime deadline = tareaData.getDeadline();
        if (deadline == null) {
            logger.info("No deadline provided; setting it to null.");
        }

        
        tareaService.nuevaTareaUsuario(idUsuario, tareaData.getTitulo(), tareaData.getDescripcion(), tareaData.getPrioridad(), tareaData.getDeadline());
        flash.addFlashAttribute("mensaje", "Tarea creada correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
    }

    @GetMapping("/usuarios/{id}/tareas")
    public String listadoTareas(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        UsuarioData usuarioLoggeado = usuarioService.findById(idUsuario);
        List<TareaData> tareas = tareaService.allTareasUsuario(idUsuario);
        model.addAttribute("usuarioLoggeado", usuarioLoggeado);
        model.addAttribute("usuario", usuarioLoggeado);
        model.addAttribute("tareas", tareas);
        return "listaTareas";
    }

    @GetMapping("/tareas/{id}/editar")
    public String formEditaTarea(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                 Model model, HttpSession session) {

        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuarioId());

        UsuarioData usuarioLoggeado = usuarioService.findById(tarea.getUsuarioId());

        model.addAttribute("usuarioLoggeado", usuarioLoggeado);
        model.addAttribute("usuario", usuarioLoggeado);
        model.addAttribute("tarea", tarea);
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setDescripcion(tarea.getDescripcion());
        tareaData.setPrioridad(tarea.getPrioridad());
        tareaData.setEstado(tarea.getEstado());
        return "formEditarTarea";
    }

    @PostMapping("/tareas/{id}/editar")
    public String grabaTareaModificada(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                       Model model, RedirectAttributes flash, HttpSession session) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        Long idUsuario = tarea.getUsuarioId();

        comprobarUsuarioLogeado(idUsuario);

        tareaService.modificaTituloTarea(idTarea, tareaData.getTitulo());
        tareaService.modificarDescripcionTarea(idTarea, tareaData.getDescripcion());
        tareaService.modificaPrioridadTarea(idTarea, tareaData.getPrioridad());
        tareaService.modificaEstadoTarea(idTarea, tareaData.getEstado());
        flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");
        return "redirect:/usuarios/" + tarea.getUsuarioId() + "/tareas";
    }

    @DeleteMapping("/tareas/{id}")
    @ResponseBody
    public String borrarTarea(@PathVariable(value="id") Long idTarea, RedirectAttributes flash, HttpSession session) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuarioId());

        tareaService.borraTarea(idTarea);
        return "";
    }

    @GetMapping("/tareas/{id}")
    public String verDetallesTarea(@PathVariable(value="id") Long idTarea, Model model, HttpSession session) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuarioId());

        UsuarioData usuarioLoggeado = usuarioService.findById(tarea.getUsuarioId());
        UsuarioData autor = usuarioService.findById(tarea.getUsuarioId());

        if (tarea.getDeadline() != null) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(now, tarea.getDeadline());
            if (duration.isNegative()) {
                duration = duration.negated();
                long days = duration.toDays();
                long hours = duration.toHours() % 24;
                long minutes = duration.toMinutes() % 60;
                String overdueTime = String.format("La tarea está retrasada por %d días y %02d horas y %02d minutos", days, hours, minutes);
                model.addAttribute("overdueTime", overdueTime);
            } else {
                long days = duration.toDays();
                long hours = duration.toHours() % 24;
                long minutes = duration.toMinutes() % 60;
                String remainingTime = String.format("Quedan %d días y %02d:%02d horas", days, hours, minutes);
                model.addAttribute("remainingTime", remainingTime);
            }
        }

        List<Comentario> comentarios = tarea.getComentarios();
        comentarios.sort((c1, c2) -> c2.getFecha().compareTo(c1.getFecha())); // Ordenar por fecha descendente

        model.addAttribute("comentarios", comentarios);
        model.addAttribute("usuarioLoggeado", usuarioLoggeado);
        model.addAttribute("usuario", usuarioLoggeado);
        model.addAttribute("tarea", tarea);
        model.addAttribute("autor", autor.getNombre());
        return "detallesTarea";
    }

    @PostMapping("/tareas/{tareaId}/comentar")
    public String agregarComentario(@PathVariable Long tareaId, @RequestParam String comentario) {
        TareaData tarea = tareaService.findById(tareaId);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        Long idUsuario = tarea.getUsuarioId();

        comprobarUsuarioLogeado(idUsuario);
        comentarioService.crearComentario(tareaId, idUsuario, comentario);
        return "redirect:/tareas/" + tareaId;
    }

    @DeleteMapping("/borrarComentario/{comentarioId}/enTarea/{tareaId}")
    @ResponseBody
    public String borrarComentario(@PathVariable Long comentarioId, @PathVariable Long tareaId) {
        TareaData tarea = tareaService.findById(tareaId);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        Long idUsuario = tarea.getUsuarioId();

        comprobarUsuarioLogeado(idUsuario);
        comentarioService.borrarComentario(comentarioId);
        return "";
    }
}