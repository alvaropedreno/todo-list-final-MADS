package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoAdminException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import madstodolist.service.UsuarioServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        if (idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }

        UsuarioData usuarioLoggeado = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuarioLoggeado);
        model.addAttribute("usuarioLoggeado", usuarioLoggeado);
        return "cuentaUsuario";
    }

    @GetMapping("/cuenta/editar")
    public String editarCuentaUsuario(Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado();
        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioLoggeado", usuario);
        return "editarCuentaUsuario";
    }


    @PostMapping("/cuenta/editar")
    public String actualizarCuentaUsuario(
            @ModelAttribute("usuarioData") @Valid UsuarioData usuarioData,
            BindingResult result,
            @RequestParam("fotoMultipartFile") MultipartFile foto, // Captura el archivo del formulario
            Model model
    ) throws IOException {
        // Validación del formulario
        if (result.hasErrors()) {
            return "actualizarCuenta"; // Vuelve al formulario si hay errores
        }

        // Procesa el archivo subido
        try {
            if (foto != null && !foto.isEmpty()) {
                usuarioData.setFotoMultipartFile(foto); // Asigna el archivo al modelo
                byte[] contenidoFoto = foto.getBytes(); // Convierte a byte[] si es necesario
                usuarioData.setFoto(contenidoFoto);     // Supongamos que tienes un campo byte[] 'foto'
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error al procesar la imagen.");
            return "actualizarCuenta"; // Vuelve al formulario con un mensaje de error
        }

        // Guardar usuarioData en la base de datos u otras acciones
        usuarioService.editarUsuario(usuarioData);
        return "redirect:/cuenta";
    }



    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }



    @PostMapping("/cuenta/cambiarPassword")
    public String cambiarPassword(@ModelAttribute UsuarioData usuarioData, Model model) {
        Long idUsuario = managerUserSession.usuarioLogeado(); // Obtener el ID del usuario logueado desde la sesión

        // Verificar que la nueva contraseña y su confirmación coincidan
        if (!usuarioData.getNewPassword().equals(usuarioData.getConfirmNewPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "cuenta/cambiarPassword";
        }

        // Cambiar la contraseña
        try {
            usuarioService.cambiarPassword(idUsuario, usuarioData.getCurrentPassword(), usuarioData.getNewPassword());
        } catch (UsuarioServiceException e) {
            model.addAttribute("error", e.getMessage());
            return "cuenta/cambiarPassword";
        }

        // Redirigir a la página de cuenta con un mensaje de éxito
        return "redirect:/cuenta?success=contraseñaActualizada";
    }



}
